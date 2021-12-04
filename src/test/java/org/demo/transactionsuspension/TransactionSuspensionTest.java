package org.demo.transactionsuspension;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.demo.bookshelf.BookRepository;
import org.demo.bookshelf.Shelf;
import org.demo.bookshelf.ShelfRepository;
import org.demo.transactionsuspension.BaseSuspensionService.SuspendedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import lombok.extern.java.Log;

// @ActiveProfiles("mariadb")
@Log
@SpringBootTest
class TransactionSuspensionTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ShelfRepository shelfRepository;

    @Autowired
    SuspendedService suspensionService;

    @Autowired
    PlatformTransactionManager tm;

    @BeforeEach
    void setup() {
        var ts = tm.getTransaction(TransactionDefinition.withDefaults());
        shelfRepository.deleteAll();
        bookRepository.deleteAll();

        shelfRepository.save(new Shelf());
        tm.commit(ts);
    }

    @Test
    void testTransactionReadRepeatableRead() {

        var shelf = shelfRepository.findAll().get(0);
        final var id = shelf.getId();

        assertDoesNotThrow(() -> suspensionService.readShelfWithConcurrentModificationRepeatableRead(id));

        shelf = shelfRepository.findById(id).orElseThrow();
        log.info("shelf: " + shelf);
    }

    @Test
    void testTransactionReadReadCommitted() {

        var shelf = shelfRepository.findAll().get(0);
        final var id = shelf.getId();

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> suspensionService.readShelfWithConcurrentModificationReadCommitted(id));

        shelf = shelfRepository.findById(id).orElseThrow();
        log.info("shelf: " + shelf);
    }

    @Test
    void testTransactionWriteRepeatableRead() {

        var shelf = shelfRepository.findAll().get(0);
        final var id = shelf.getId();

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> suspensionService.writeShelfWithConcurrentModification(id));

        shelf = shelfRepository.findById(id).orElseThrow();
        log.info("shelf: " + shelf);
    }

}

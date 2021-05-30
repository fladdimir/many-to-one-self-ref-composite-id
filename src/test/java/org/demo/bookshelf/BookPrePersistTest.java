package org.demo.bookshelf;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class BookPrePersistTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ShelfRepository shelfRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setup() {
        shelfRepository.deleteAll();
        bookRepository.deleteAll();
        var books = bookRepository.findAll();
        assertThat(books).isEmpty();
    }

    @Test
    void testSingle() {
        var book = new Book();
        book = bookRepository.save(book);

        assertThat(book.getCreatedAt()).isNotNull();
        assertThat(book.getCreatedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    void testList() {

        List<Book> unsavedObjects = new ArrayList<>();
        unsavedObjects.add(new Book());

        List<Book> savedObjects = new ArrayList<>();

        // note: no currently running transaction until now
        transactionTemplate.execute(status -> {
            unsavedObjects.forEach(obj -> savedObjects.add(bookRepository.save(obj)));
            return unsavedObjects;
        });

        var book = savedObjects.get(0);
        assertThat(book.getCreatedAt()).isNotNull();
        assertThat(book.getCreatedAt()).isBefore(LocalDateTime.now());
    }
}

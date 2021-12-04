package org.demo.transactionsuspension;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.demo.bookshelf.Shelf;
import org.demo.bookshelf.ShelfRepository;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.java.Log;

@Log
public class BaseSuspensionService {

    @Autowired
    ShelfRepository shelfRepository;

    @Autowired
    EntityManager entityManager;

    @Service
    public static class SuspendedService extends BaseSuspensionService {

        @Autowired
        SuspendingService suspendingService;

        @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
        public void readShelfWithConcurrentModificationRepeatableRead(Long id) {
            readShelfWithConcurrentModification(id);
        }

        @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
        public void readShelfWithConcurrentModificationReadCommitted(Long id) {
            readShelfWithConcurrentModification(id);
        }

        private void readShelfWithConcurrentModification(Long id) {
            readShelf(id);

            // + modify in own transaction which commits before this one
            suspendingService.modifyShelfNewTx(id);

            log.info("\n\n\ntx2 already commited, tx1 is about to commit\n\n\n");
            // commit suspended transaction on exit
            // -> reading again to see whether optimistically locked entity is still
            // unmodified -> throws exception if not isolation level repeatable read
        }

        @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
        public void writeShelfWithConcurrentModification(Long id) {
            setShelfName(readShelf(id), "tx1");

            // + modify in own transaction which commits before this one
            suspendingService.modifyShelfNewTx(id);

            log.info("\n\n\ntx2 already commited, tx1 is about to flush\n\n\n");
            shelfRepository.flush();
            // -> update with 0 rows returned (1 expected) causes exception!

            log.info("\n\n\ntx2 already commited, tx1 is about to commit\n\n\n");
            // commit suspended transaction on exit
        }
    }

    @Service
    public static class SuspendingService extends BaseSuspensionService {

        @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
        public void modifyShelfNewTx(Long id) {
            setShelfName(readShelf(id), "tx2");
            logTxLevel();
        }
    }

    Shelf readShelf(Long id) {
        logTxLevel();
        Shelf shelf = shelfRepository.findById(id).orElseThrow();
        log.info("\n\nold name: " + shelf.getName() + ", version: " + shelf.getVersion()
                + "\n\n");
        return shelf;
    }

    void setShelfName(Shelf shelf, String name) {
        log.info("\n\nold name: " + shelf.getName() + ", new name: " + name + ", version: " + shelf.getVersion()
                + "\n\n");
        shelf.setName(name);
        shelfRepository.save(shelf);
    }

    void logTxLevel() {
        Session session = entityManager.unwrap(Session.class);
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                log.info("\n\n\nTransaction isolation level: "
                        + ConnectionProviderInitiator.toIsolationNiceName(connection.getTransactionIsolation())
                        + "\n\n\n");
            }
        });
    }
}
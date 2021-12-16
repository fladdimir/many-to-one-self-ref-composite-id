package org.demo.unique;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@SpringBootTest
class UniqueTest {

    @Autowired
    private PlatformTransactionManager tm;

    @Autowired
    private EntityWithUniqueNameRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.deleteAllInBatch();
    }

    @Test
    void test() {
        var txdef = new DefaultTransactionDefinition();
        ((AbstractPlatformTransactionManager) tm).setGlobalRollbackOnParticipationFailure(false);
        ((AbstractPlatformTransactionManager) tm).setFailEarlyOnGlobalRollbackOnly(false);
        var ts = tm.getTransaction(txdef);

        var entity1 = new EntityWithUniqueName();
        entity1.setUniqueName("uniqueName");
        repository.saveAndFlush(entity1);

        var entity2 = new EntityWithUniqueName();
        entity2.setUniqueName("uniqueName");

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(entity2));

        assertThrows(UnexpectedRollbackException.class, () -> tm.commit(ts));

        assertThat(repository.count()).isZero();
    }

}

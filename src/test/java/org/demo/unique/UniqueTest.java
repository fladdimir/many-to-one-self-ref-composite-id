package org.demo.unique;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
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

    // @BeforeEach // -> would delete rows just inserted by @Sql
    void cleanup() {
        repository.deleteAllInBatch();
    }

    @AfterEach
    void afterEach() {
        cleanup();
    }

    @Test
    void test() {
        cleanup();

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

    @Sql(statements = "INSERT INTO entity_with_unique_name (unique_name, id) VALUES ('uniqueName', 1)", //
            executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, //
            config = @SqlConfig(transactionMode = TransactionMode.INFERRED, errorMode = ErrorMode.FAIL_ON_ERROR))
    @Transactional
    @Test
    void testSqlSetup() {
        assertThat(repository.count()).isEqualTo(1);
    }

}

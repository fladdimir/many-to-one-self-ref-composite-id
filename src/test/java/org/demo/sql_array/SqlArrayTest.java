package org.demo.sql_array;

import static org.assertj.core.api.Assertions.assertThat;

import javax.transaction.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;

@SpringBootTest
class SqlArrayTest {

    @Autowired
    private SqlArrayEntityRepository repository;

    @Sql(statements = "DELETE FROM sql_array_entity;" //
            + "INSERT INTO sql_array_entity (id, ints) VALUES (1, ARRAY[2,3]), (2, ARRAY[4]);" //
            , executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, //
            config = @SqlConfig(transactionMode = TransactionMode.ISOLATED, errorMode = ErrorMode.FAIL_ON_ERROR))
    @Transactional
    @Rollback(false)
    @Test
    void testSqlSetup() {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(Expressions.booleanTemplate("function('array_length', {0}, 1)", QSqlArrayEntity.sqlArrayEntity.ints)
                .castToNum(Integer.class).gt(1));

        var result = repository.findAll(builder);
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(1);
        assertThat(result.iterator().next().getInts()).containsExactly(2, 3);
    }

}

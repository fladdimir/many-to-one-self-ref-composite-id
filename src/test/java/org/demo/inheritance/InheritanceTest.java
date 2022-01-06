package org.demo.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;

@Sql(statements = InheritanceTest.SETUP_STRING, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, //
        config = @SqlConfig(transactionMode = TransactionMode.ISOLATED, errorMode = ErrorMode.FAIL_ON_ERROR))
@SpringBootTest
class InheritanceTest {

    @Autowired
    private SuperClassRepository superClassRepository;
    @Autowired
    private Child1Repository child1Repository;
    @Autowired
    private Child2Repository child2Repository;

    static final String SETUP_STRING = "" //
            + "DELETE FROM child1;" //
            + "DELETE FROM child2;" //
            + "DELETE FROM super_class;" //
            + "INSERT INTO super_class (id, super_attribute) VALUES (1, 33), (2, 33);" //
            + "INSERT INTO child1 (id, child_attribute1) VALUES (1, 11);" //
            + "INSERT INTO child2 (id, child_attribute2) VALUES (2, 22);" //
    ;

    @Test
    // @Transactional
    void test() {
        Child1 child1 = child1Repository.findById(1).get();
        Child2 child2 = child2Repository.findById(2).get();
        assertThat(child1.getChildAttribute1()).isEqualTo(11);
        assertThat(child1.getSuperAttribute()).isEqualTo(33);
        assertThat(child2.getChildAttribute2()).isEqualTo(22);
        assertThat(child2.getSuperAttribute()).isEqualTo(33);

        SuperClass sc1 = superClassRepository.findById(1).get();
        SuperClass sc2 = superClassRepository.findById(2).get();
        assertThat(sc1.getSuperAttribute()).isEqualTo(33);
        assertThat(sc2.getSuperAttribute()).isEqualTo(33);

        assertThat(superClassRepository.getDistinctSuperAttributes()).containsExactly(33);
        assertThat(superClassRepository.getSomeChildAttributesViaNativeQueryUnion()).containsExactlyInAnyOrder(11, 22);
    }
}

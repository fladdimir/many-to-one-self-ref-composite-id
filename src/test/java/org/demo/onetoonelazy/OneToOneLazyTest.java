package org.demo.onetoonelazy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

@SpringBootTest
class OneToOneLazyTest {

    @Autowired
    private LazyARepository aRepository;
    @Autowired
    private LazyBRepository bRepository;
    @Autowired
    private PlatformTransactionManager tm;

    @BeforeEach
    void beforeEach() {
        aRepository.deleteAllInBatch();
        bRepository.deleteAllInBatch();

        var a = aRepository.save(new LazyA());
        var b = new LazyB();
        b.setMyValue("bbb");
        bRepository.save(b);
        a.setLazyB(b);
        a = aRepository.save(a);
        aid = a.getId();
    }

    Long aid;

    @Test
    void test_lazy() {

        var ts = tm.getTransaction(TransactionDefinition.withDefaults());

        System.out.print("\nFIND A BY ID\n");
        var a = aRepository.findById(aid).orElseThrow(); // no join fetch
        System.out.print("\nACCESS B VALUE\n");
        Assertions.assertThat(a.getLazyB().getMyValue()).isEqualTo("bbb"); // extra query for lazy prop b

        tm.commit(ts);
    }

    @Test
    void test_joinFetch() {

        var ts = tm.getTransaction(TransactionDefinition.withDefaults());

        System.out.print("\nFIND A BY ID\n");
        var a = aRepository.getLazyA(aid); // join fetch
        System.out.print("\nACCESS B VALUE\n");
        Assertions.assertThat(a.getLazyB().getMyValue()).isEqualTo("bbb"); // no query here

        tm.commit(ts);
    }
}

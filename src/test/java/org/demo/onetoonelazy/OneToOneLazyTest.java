package org.demo.onetoonelazy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import lombok.Data;

@SpringBootTest
class OneToOneLazyTest {

    @Autowired
    private LazyARepository aRepository;
    @Autowired
    private LazyBRepository bRepository;
    @Autowired
    private CRepository cRepository;
    @Autowired
    private PlatformTransactionManager tm;

    @BeforeEach
    void beforeEach() {

        aRepository.deleteAllInBatch();
        bRepository.deleteAllInBatch();
        cRepository.deleteAllInBatch();

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
        System.out.print("\nACCESS B ID\n");
        var id = a.getLazyB().getId(); // does not trigger a db query
        assertThat(id).isNotNull();
        System.out.print("\nACCESS B VALUE\n");
        assertThat(a.getLazyB().getMyValue()).isEqualTo("bbb"); // extra query for lazy prop b

        tm.commit(ts);
    }

    @Test
    void test_joinFetch() {

        var ts = tm.getTransaction(TransactionDefinition.withDefaults());

        System.out.print("\nFIND A BY ID\n");
        var a = aRepository.getLazyA(aid); // join fetch
        System.out.print("\nACCESS B VALUE\n");
        assertThat(a.getLazyB().getMyValue()).isEqualTo("bbb"); // no query here

        tm.commit(ts);
    }

    @Data
    static class LazyADto1 {
        private Long id;
        private Long lazyBId;
        private String lazyBMyValue;
        private Long cid;
        private String cValue;
    }

    @Test
    void test_modelMapperNested() {
        var c = new C();
        c.setCValue("c");
        var a = aRepository.getLazyA(aid);
        a.getLazyB().setC(c);
        aRepository.save(a);

        var ts = tm.getTransaction(TransactionDefinition.withDefaults());

        a = aRepository.getLazyA(aid);
        assertThat(a.getLazyB().getC().getCValue()).isEqualTo("c");

        ModelMapper mm = new ModelMapper();
        mm.typeMap(LazyA.class, LazyADto1.class).addMappings(mapper -> {
            mapper.map(src -> src.getLazyB().getC().getId(), LazyADto1::setCid);
            mapper.map(src -> src.getLazyB().getC().getCValue(), LazyADto1::setCValue);
        });
        var ladto = mm.map(a, LazyADto1.class);
        assertThat(ladto.getCValue()).isEqualTo("c");

        tm.commit(ts);
    }
}

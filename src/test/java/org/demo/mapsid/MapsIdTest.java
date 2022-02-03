package org.demo.mapsid;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MapsIdTest {

    @Autowired
    private ARepository aRepository;
    @Autowired
    private BRepository bRepository;

    @BeforeEach
    void before() {
        bRepository.deleteAllInBatch();
        aRepository.deleteAllInBatch();
    }

    @Test
    void test() {

        A a = new A();
        B b = new B();
        a.setB(b);
        b.setA(a);

        a = aRepository.save(a);
        var aid = a.getId();

        a = aRepository.findById(aid).orElseThrow();
        assertThat(a.getB().getA()).isSameAs(a);
        assertThat(a.getB().getId()).isEqualTo(aid);
    }
}

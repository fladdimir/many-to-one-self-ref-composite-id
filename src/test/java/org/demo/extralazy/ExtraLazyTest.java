package org.demo.extralazy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

@SpringBootTest
class ExtraLazyTest {

    @Autowired
    private ElParentRepository parentRepository;

    @Autowired
    private ElChildRepository childRepository;

    @Autowired
    private PlatformTransactionManager tm;

    private static final int N = 5;

    @BeforeEach
    void before() {
        childRepository.deleteAllInBatch();
        parentRepository.deleteAllInBatch();

        createParentWithNChildren();

        assertAllParentsHaveNChildren();
    }

    private void assertAllParentsHaveNChildren() {
        var ts = tm.getTransaction(TransactionDefinition.withDefaults());
        parentRepository.findAll().forEach(p -> assertThat(p.getChildren()).hasSize(N));
        tm.commit(ts);
    }

    private void createParentWithNChildren() {
        final ElParent parent = parentRepository.save(new ElParent());
        pid = parent.getId();

        parent.getChildren()
                .addAll(IntStream.rangeClosed(1, N).mapToObj(i -> new ElChild("" + i, parent))
                        .collect(Collectors.toList()));
        parentRepository.save(parent);
    }

    Long pid;

    @Test
    void test() {
        var ts = tm.getTransaction(TransactionDefinition.withDefaults());

        System.out.println("\nFIND BY ID\n");
        var parent = parentRepository.findById(pid).orElseThrow();

        System.out.println("\nCOLLECTION SIZE\n");
        var csize = parent.getChildren().size();

        for (int i = 0; i < csize; i++) {
            System.out.println("\nELEMENT ACCESS\n");
            var child = parent.getChildren().get(i);
            assertThat(child.getMyValue()).isEqualTo("" + (i + 1));
        }

        tm.commit(ts);
    }

    @Test
    void testMoreParents() {
        createParentWithNChildren();
        createParentWithNChildren();
        assertAllParentsHaveNChildren();
    }
}

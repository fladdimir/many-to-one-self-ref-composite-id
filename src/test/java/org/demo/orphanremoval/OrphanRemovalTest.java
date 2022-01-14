package org.demo.orphanremoval;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

@SpringBootTest
class OrphanRemovalTest {

    @Autowired
    OrphanParentRepository parentRepository;

    @Autowired
    OrphanChildRepository childRepository;

    @Autowired
    PlatformTransactionManager tm;

    Long parentId;

    @BeforeEach
    void beforeEach() {
        childRepository.deleteAllInBatch();
        parentRepository.deleteAllInBatch();
        parentId = null;
    }

    void setupParentWith2Children() {
        var parent = parentRepository.save(new OrphanParent());
        parentId = parent.getId();

        var ts = tm.getTransaction(TransactionDefinition.withDefaults());

        var child1 = new OrphanChild();
        child1.setUniqueStringValue("c1");
        var child2 = new OrphanChild();
        child2.setUniqueStringValue("c2");
        child1.setParent(parent);
        child2.setParent(parent);
        parent.getChildren().add(child1);
        parent.getChildren().add(child2);
        parentRepository.save(parent);

        tm.commit(ts);

        ts = tm.getTransaction(TransactionDefinition.withDefaults());
        assertThat(parentRepository.findById(parentId).get().getChildren()).hasSize(2);
        tm.commit(ts);
    }

    @Test
    void testSimpleOrphanRemoval() {

        setupParentWith2Children();

        var ts = tm.getTransaction(TransactionDefinition.withDefaults());
        parentRepository.findById(parentId).get().getChildren().clear();
        tm.commit(ts);

        ts = tm.getTransaction(TransactionDefinition.withDefaults());
        assertThat(parentRepository.findById(parentId).get().getChildren()).isEmpty();
        assertThat(childRepository.findAll()).isEmpty();
        tm.commit(ts);
    }

    @Test
    void testOrphanRemovalAndCreateNewChildren() {

        setupParentWith2Children();

        var ts = tm.getTransaction(TransactionDefinition.withDefaults());

        var child3 = new OrphanChild();
        child3.setUniqueStringValue("c2"); // same as child2, which is not yet deleted

        var parent = parentRepository.findById(parentId).get();
        child3.setParent(parent);

        parent.getChildren().clear();

        parentRepository.flush(); // <-- this flush deletes the orphans before the insert is triggered by the
                                  // cascade below

        parent.getChildren().add(child3);
        tm.commit(ts); // exception if not flushed before

        ts = tm.getTransaction(TransactionDefinition.withDefaults());
        assertThat(parentRepository.findById(parentId).get().getChildren()).hasSize(1);
        assertThat(childRepository.findAll()).hasSize(1);
        tm.commit(ts);
    }
}

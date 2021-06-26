package org.demo.softdeletecompositekey;

import org.demo.softdeletecompositekey.SoftDeleteCompositeKeyTestEntity.TestCompositeEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestSoftDeleteCompositeEntityRepository
        extends JpaRepository<SoftDeleteCompositeKeyTestEntity, TestCompositeEntityId> {

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM soft_delete_composite_key_test_entity WHERE deleted=true")
    long countSoftDeleted();
}

package org.demo.inheritance;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SuperClassRepository extends JpaRepository<SuperClass, Integer> {

    @Query("SELECT DISTINCT superAttribute FROM SuperClass")
    Set<Integer> getDistinctSuperAttributes();

    @Query(value = "SELECT DISTINCT child_attribute1 FROM child1 UNION SELECT DISTINCT child_attribute2 FROM child2;", nativeQuery = true)
    Set<Integer> getSomeChildAttributesViaNativeQueryUnion();
}

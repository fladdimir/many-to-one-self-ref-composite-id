package org.demo.extralazy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElParentRepository extends JpaRepository<ElParent, Long> {

}

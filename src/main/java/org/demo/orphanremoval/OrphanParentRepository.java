package org.demo.orphanremoval;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrphanParentRepository extends JpaRepository<OrphanParent, Long> {

}

package org.demo.batchinsert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UuidEntityRepository extends JpaRepository<UuidEntity, Long> {

}

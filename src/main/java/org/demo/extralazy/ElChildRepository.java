package org.demo.extralazy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElChildRepository extends JpaRepository<ElChild, Long> {

}

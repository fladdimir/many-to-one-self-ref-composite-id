package org.demo.jsonsort;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JsonSortRepository extends JpaRepository<JsonSortEntity, Long> {
}

package org.demo.inheritance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Child1Repository extends JpaRepository<Child1, Integer> {

}

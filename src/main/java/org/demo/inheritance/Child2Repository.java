package org.demo.inheritance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Child2Repository extends JpaRepository<Child2, Integer> {

}

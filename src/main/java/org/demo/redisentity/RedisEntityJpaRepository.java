package org.demo.redisentity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisEntityJpaRepository extends JpaRepository<RedisEntityJpa, Long> {

}

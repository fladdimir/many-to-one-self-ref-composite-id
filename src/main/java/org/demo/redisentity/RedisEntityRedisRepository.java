package org.demo.redisentity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisEntityRedisRepository extends CrudRepository<RedisEntityRedis, Long> {

}

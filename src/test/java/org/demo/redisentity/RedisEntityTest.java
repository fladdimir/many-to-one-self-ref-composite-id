package org.demo.redisentity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisEntityTest {

    @Autowired
    private RedisEntityJpaRepository jpaRepository;

    @Autowired
    private RedisEntityRedisRepository redisRepository;

    @BeforeEach
    void beforeEach() {
        jpaRepository.deleteAllInBatch();
        assertThat(jpaRepository.count()).isZero();
        redisRepository.deleteAll();
        assertThat(redisRepository.count()).isZero();
    }

    @Test
    void test1_rdb() {
        String name = UUID.randomUUID().toString();
        var entity = new RedisEntityJpa();
        entity.setName(name);
        jpaRepository.save(entity);

        assertThat(jpaRepository.findAll().get(0).getName()).isEqualTo(name);
    }

    @Test
    void test1_redis() {
        String name = UUID.randomUUID().toString();
        var entity = new RedisEntityRedis();
        entity.setName(name);
        redisRepository.save(entity);

        assertThat(redisRepository.findAll().iterator().next().getName()).isEqualTo(name);
    }
}

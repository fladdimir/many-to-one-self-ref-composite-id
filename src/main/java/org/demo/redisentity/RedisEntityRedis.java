package org.demo.redisentity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash
@Data
@NoArgsConstructor
public class RedisEntityRedis {

    @Id // not generated
    private Long id;

    private String name;

}

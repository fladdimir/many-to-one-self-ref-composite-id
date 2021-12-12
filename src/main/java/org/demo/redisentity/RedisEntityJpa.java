package org.demo.redisentity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RedisEntityJpa {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

}

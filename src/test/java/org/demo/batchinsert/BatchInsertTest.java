package org.demo.batchinsert;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class BatchInsertTest {

  @Autowired
  UuidEntityRepository repository;

  @BeforeEach
  public void setup() {
    repository.deleteAllInBatch();
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void testBatchInsert() {
    System.out.println("\nCreating new entities...\n");
    int n = 15;
    var entities = Stream.generate(UuidEntity::new).limit(n).collect(Collectors.toList());
    System.out.println("\nSaving new entities...\n");
    repository.saveAll(entities);
    System.out.println("\nDone.\n");

    assertThat(repository.findAll()).hasSize(n);
  }

}

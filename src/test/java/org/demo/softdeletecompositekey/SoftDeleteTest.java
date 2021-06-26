package org.demo.softdeletecompositekey;

import static org.assertj.core.api.Assertions.assertThat;

import org.demo.softdeletecompositekey.SoftDeleteCompositeKeyTestEntity.TestCompositeEntityId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class SoftDeleteTest {

	@Autowired
	TestSoftDeleteCompositeEntityRepository repository;

	@BeforeEach
	public void setup() {
		repository.deleteAllInBatch();
		assertThat(repository.findAll()).isEmpty();
		assertThat(repository.countSoftDeleted()).isZero();
	}

	@Test
	void test() {
		var entity1 = new SoftDeleteCompositeKeyTestEntity();
		entity1.setKey1(1L);
		entity1.setKey2("2");
		repository.save(entity1);
		assertThat(repository.findAll()).hasSize(1);

		repository.deleteById(new TestCompositeEntityId(1L, "2"));
		assertThat(repository.findAll()).isEmpty();
		assertThat(repository.countSoftDeleted()).isEqualTo(1);
	}

}

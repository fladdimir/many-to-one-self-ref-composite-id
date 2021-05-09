package org.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class DemoApplicationTests {

	@Autowired
	UserEntityRepository repository;

	private UserEntity createEntity(Integer id) {
		var entity = new UserEntity();
		entity.setId(id);
		entity.setFirstName(id.toString());
		entity.setLastName(id.toString());
		return entity;
	}

	private Optional<UserEntity> findOptionalById(Integer id) {
		var compositeId = new UserEntityCompositeId(id, id.toString(), id.toString());
		return repository.findById(compositeId);
	}

	private UserEntity findById(Integer id) {
		return findOptionalById(id).orElseThrow();
	}

	@BeforeEach
	public void setup() {
		// delete children before parents (if present)
		Stream.of(3, 2, 1, 0).map(this::findOptionalById).forEach(o -> o.ifPresent(repository::delete));

		// create 4 entities and setup some relations
		var e0 = createEntity(0);
		var e1 = createEntity(1);
		var e2 = createEntity(2);
		var e3 = createEntity(3);

		repository.save(e0);// insert parents before children
		// 0 -> 1
		e0.addChild(e1);
		repository.save(e1);

		// 0 -> 2 -> 3
		e0.addChild(e2);
		repository.save(e2);
		e3.setParent(e2); // delegating to 'addChild' of the parent
		repository.save(e3);
	}

	@Test
	public void testFindAllWithRelations() {
		var all = repository.findAll();
		assertThat(all).hasSize(4);

		var e0 = findById(0);
		var e1 = findById(1);
		var e2 = findById(2);
		var e3 = findById(3);

		assertThat(e0.getChildren()).containsExactlyInAnyOrder(e1, e2);
		assertThat(e1.getParent()).isEqualTo(e0);
		assertThat(e2.getParent()).isEqualTo(e0);

		assertThat(e2.getChildren()).containsExactlyInAnyOrder(e3);
		assertThat(e3.getParent()).isEqualTo(e2);
	}

}

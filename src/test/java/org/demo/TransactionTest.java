package org.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class TransactionTest {

	@Autowired
	UserEntityRepository repository;

	@Autowired
	EntityManager entityManager;

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

	@BeforeEach
	void clean() {
		Stream.of(3, 2, 1, 0).map(this::findOptionalById).forEach(o -> o.ifPresent(repository::delete));
		repository.flush();
		assertThat(repository.findAll()).isEmpty();
	}

	@Test
	@Transactional // creates new transaction when starting the test
	@Rollback(false) // commit at end of test (not needed, just for manual DB inspection afterwards)
	void testSessionWithTransactional() {
		Session session = entityManager.unwrap(Session.class);
		assertThat(session.isDirty()).isFalse();

		var newEntity = createEntity(1);
		assertThat(session.isDirty()).isFalse();

		repository.save(newEntity); // transaction already active
		assertThat(session.isDirty()).isTrue(); // -> change not yet flushed!
		repository.flush();
		assertThat(session.isDirty()).isFalse();
	}

	@Test
	void testSessionWithoutTransactional() {
		Session session = entityManager.unwrap(Session.class);
		assertThat(session.isDirty()).isFalse();

		var newEntity = createEntity(1);
		assertThat(session.isDirty()).isFalse();

		repository.save(newEntity); // creates and commits a transaction
		assertThat(session.isDirty()).isFalse(); // insertion already flushed
	}

}

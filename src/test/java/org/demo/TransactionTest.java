package org.demo;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

// 'docker-compose up' before running the tests
@SpringBootTest
class TransactionTest {

	@Autowired
	UserEntityRepository repository;

	@Autowired
	EntityManager entityManager;

	@BeforeEach
	public void cleanDb() {
		repository.deleteAll();
		repository.flush();
		assertThat(repository.findAll()).isEmpty();
	}

	@Test
	@Transactional // creates new transaction when starting the test
	@Rollback(false) // commit at end of test (not needed, just for manual DB inspection afterwards)
	public void testSessionWithTransactional() {
		Session session = entityManager.unwrap(Session.class);
		assertThat(session.isDirty()).isFalse();

		var newEntity = new UserEntity(1L);
		assertThat(session.isDirty()).isFalse();

		repository.save(newEntity); // transaction already active and propagated
		assertThat(session.isDirty()).isTrue(); // -> change not yet flushed!
		repository.flush();
		assertThat(session.isDirty()).isFalse();
	}

	@Test
	public void testSessionWithoutTransactional() {
		Session session = entityManager.unwrap(Session.class);
		assertThat(session.isDirty()).isFalse();

		var newEntity = new UserEntity(1L);
		assertThat(session.isDirty()).isFalse();

		repository.save(newEntity); // creates and commits a transaction
		assertThat(session.isDirty()).isFalse(); // insertion already flushed
	}

}

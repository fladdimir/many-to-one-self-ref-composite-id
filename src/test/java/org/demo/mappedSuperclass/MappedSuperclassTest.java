package org.demo.mappedSuperclass;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class MappedSuperclassTest {

	@Autowired
	UserProfileRepository repository;

	@BeforeEach
	public void setup() {
		repository.deleteAll();
		repository.flush();

		var profile = new UserProfile();
		var contactDetails = new ContactDetails();
		profile.setContactDetails(contactDetails);
		repository.save(profile);
	}

	@Test
	void testFindAllWithRelations() {
		var all = repository.findAll();
		assertThat(all).hasSize(1);
		var profile = all.get(0);
		assertThat(profile.getContactDetails()).isNotNull();
	}

}

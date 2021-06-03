package org.demo.roles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.support.TransactionTemplate;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class RolesTest {

	@Autowired
	PermissionRepository permissionRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	TransactionTemplate tt;

	private long roleId1;
	private long roleId2;

	@BeforeEach
	public void setup() {
		List.of(roleRepository, permissionRepository).forEach(JpaRepository::deleteAllInBatch);

		// role1 -> perm1
		// role2 -> perm1
		// role2 -> perm2

		var role1 = new RoleEntity();
		var role2 = new RoleEntity();
		var perm1 = new PermissionEntity();
		var perm2 = new PermissionEntity();

		List.of(role1, role2).forEach(roleRepository::save);
		List.of(perm1, perm2).forEach(permissionRepository::save);

		role1.getPermissions().add(perm1);
		role2.getPermissions().add(perm1);
		role2.getPermissions().add(perm2);
		List.of(role1, role2).forEach(roleRepository::save);

		roleId1 = role1.getId();
		roleId2 = role2.getId();
	}

	@Test
	void testSetup() {
		assertThat(roleRepository.findAll()).hasSize(2);
		assertThat(permissionRepository.findAll()).hasSize(2);
	}

	@Test
	void testRemoval1() {
		tt.executeWithoutResult(status -> {
			assertThat(roleRepository.findById(roleId1).get().getPermissions()).hasSize(1);
		});

		roleRepository.delete(roleRepository.findById(roleId1).get());
		assertThat(permissionRepository.findAll()).hasSize(2); // no permission removed
	}

	@Test
	void testRemoval2() {
		tt.executeWithoutResult(status -> {
			assertThat(roleRepository.findById(roleId2).get().getPermissions()).hasSize(2);
		});
		roleRepository.delete(roleRepository.findById(roleId2).get());
		assertThat(permissionRepository.findAll()).hasSize(1); // perm2 removed
	}

}

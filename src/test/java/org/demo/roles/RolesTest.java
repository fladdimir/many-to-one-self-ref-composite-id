package org.demo.roles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

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
	UserRepository userRepository;
	@Autowired
	PermissionRepository permissionRepository;
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	TransactionTemplate tt;

	long userId1;
	long userId2;

	@BeforeEach
	public void clean() {
		List.of(userRepository, roleRepository, permissionRepository).forEach(JpaRepository::deleteAllInBatch);

		// user1 -> role1 -> perm1

		// user2 -> role1 -> perm1
		// user2 -> role2 -> perm2
		// user2 -> role3 -> perm3
		// user2 -> role3 -> perm4

		var user1 = new UserEntity();
		var user2 = new UserEntity();
		var role1 = new RoleEntity();
		var role2 = new RoleEntity();
		var role3 = new RoleEntity();
		var perm1 = new PermissionEntity();
		var perm2 = new PermissionEntity();
		var perm3 = new PermissionEntity();
		var perm4 = new PermissionEntity();

		perm1.setName("perm1");
		perm2.setName("perm2");
		perm3.setName("perm3");
		perm4.setName("perm4");

		List.of(user1, user2).forEach(userRepository::save);
		userId1 = user1.getId();
		userId2 = user2.getId();
		List.of(role1, role2, role3).forEach(roleRepository::save);
		List.of(perm1, perm2, perm3, perm4).forEach(permissionRepository::save);

		role1.getPermissions().add(perm1);
		role2.getPermissions().add(perm2);
		role3.getPermissions().add(perm3);
		role3.getPermissions().add(perm4);
		List.of(role1, role2, role3).forEach(roleRepository::save);

		user1.getRoles().add(role1);
		user2.getRoles().add(role1);
		user2.getRoles().add(role2);
		user2.getRoles().add(role3);
		List.of(user1, user2).forEach(userRepository::save);
	}

	@Test
	void testUser1() {
		print("start test 1");

		tt.executeWithoutResult(status -> {
			var user1 = userRepository.findFooById(userId1).get();

			print("assert");

			assertThat(
					user1.getRoles().stream().flatMap(r -> r.getPermissions().stream()).map(PermissionEntity::getName))
							.containsExactlyInAnyOrder("perm1");
		});
		print("test 1 done");
	}

	@Test
	void testUser2() {
		print("start test 2");
		tt.executeWithoutResult(status -> {
			// var user2 = userRepository.findWithPermissionsById(userId2).get();
			var user2 = userRepository.findFooById(userId2).get();

			print("assert roles");

			var roles = user2.getRoles();
			assertThat(roles.stream().map(RoleEntity::getName)).hasSize(3);

			print("assert permissions");

			var permissions = roles.stream().flatMap(r -> r.getPermissions().stream()).collect(Collectors.toList());
			assertThat(permissions.stream().map(PermissionEntity::getName)).containsExactlyInAnyOrder("perm1", "perm2",
					"perm3", "perm4");
		});
		print("test 2 done");
	}

	private void print(String line) {
		System.out.println("\n### " + line + " ###\n");
	}

}

package com.example.currencybank;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
class TestApplicationTests {
//
//	@Autowired
//	private TestEntityManager entityManager;
//
//	@Autowired
//	private UserRepository userRepo;
//
//	@Autowired
//	private RoleRepository roleRepo;
//
//	@Test
//	public void testCreateUser() {
//		User user = new User();
//		user.setUsername("admin");
//		user.setPassword("1234");
//		user.setName("Админ Админыч");
//		user.setEnabled(true);
//		user.setLocked(false)
//		Role role = roleRepo.findByName("ROLE_ADMIN");
//		if (role == null) {
//			Role roletemp = new Role();
//			roletemp.setName("ROLE_ADMIN");
//			role = roleRepo.save(roletemp);
//		}
//		user.setRoles(List.of(role));
//		User savedUser = userRepo.save(user);
//
//		User existUser = entityManager.find(User.class, savedUser.getId());
//
//		assertThat(user.getUsername()).isEqualTo(existUser.getUsername());
//		assertThat(savedUser.getRoles().size()).isEqualTo(1);
//
//	}
}

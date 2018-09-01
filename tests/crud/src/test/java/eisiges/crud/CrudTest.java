package eisiges.crud;

import io.thorntail.test.ThorntailTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import static org.junit.Assert.*;
import org.junit.Before;

@RunWith(ThorntailTestRunner.class)
public class CrudTest {
	@Inject
	UserGEM users;

	@PersistenceContext
	EntityManager em;

	@Before
	public void beforeEach() {
		em.getTransaction().begin();
		em.createQuery("DELETE FROM UserModel");
		em.getTransaction().commit();
	}

	@Test
	@Transactional
	public void testCreate() {
		Long userId = users.persist(UserModel
		    .builder()
		    .username("hmcgroin")
		    .fullName("Holden McGroin")
		    .birthDate(Calendar.getInstance())
		    .build()).getId();

		assertNotNull(em.find(UserModel.class, userId));
	}

	@Test
	@Transactional
	public void testGet() {
		UserModel bdUser = UserModel
		    .builder()
		    .username("bdover")
		    .fullName("Ben Dover")
		    .birthDate(Calendar.getInstance())
		    .build();
		em.persist(bdUser);
		em.flush();
		em.clear();

		assertNotNull(users.get(bdUser.getId()));
	}

	@Test
	@Transactional
	public void testFindMatchingWithString() {
		UserModel it = UserModel.builder()
		    .username("pfile")
		    .fullName("Peter File")
		    .birthDate(Calendar.getInstance())
		    .build();
		em.persist(it);
		em.flush();

		UserModel keyObject = UserModel.builder().fullName("Peter File").build();

		UserModel found = users.getMatching(keyObject).get(0);
		assertTrue(found.equals(it));
	}
}


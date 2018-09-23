package eisiges.generator;

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
public class GeneratorTest {
	@Inject
	UserModelDAO users;

	@Inject
	CustomEntityDAO customEntities;

	@PersistenceContext
	EntityManager em;

	@Before
	public void beforeEach() {
		em.getTransaction().begin();
		em.createQuery("DELETE FROM UserModel").executeUpdate();
		em.createQuery("DELETE FROM UngeneratedModel").executeUpdate();
		em.getTransaction().commit();
	}

	@Test(expected = ClassNotFoundException.class)
	public void testGenerationDisabled() throws ClassNotFoundException {
		String ungeneratedClassFullyQualified = "eisiges.generator.UngeneratedModel";
		GeneratorTest.class.getClassLoader().loadClass(ungeneratedClassFullyQualified + "DAO");
	}

	@Test
	public void testAlwaysGenerateQueryBuilder() throws ClassNotFoundException { //don't always generate QueryBuilder
		/*String ungeneratedClassFullyQualified = "eisiges.generator.UngeneratedModel";
		GeneratorTest.class.getClassLoader().loadClass(ungeneratedClassFullyQualified + "QueryBuilder");*/
		
		String userClassFullyQualified = "eisiges.generator.UserModel";
		GeneratorTest.class.getClassLoader().loadClass(userClassFullyQualified + "QueryBuilder");
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

	@Test
	public void testGeneratedDaoHasCustomParent() {
		assertEquals("Custom Method Result", customEntities.customMethod());
	}
}


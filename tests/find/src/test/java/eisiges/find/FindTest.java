package eisiges.find;

import io.thorntail.test.ThorntailTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import static org.junit.Assert.*;
import org.junit.Before;

@RunWith(ThorntailTestRunner.class)
public class FindTest {
	@Inject
	UserModelDAO users;

	@PersistenceContext
	EntityManager em;

	@Before
	@Transactional
	public void beforeEach() {
		em.createQuery("DELETE FROM UserModel").executeUpdate();
	}

	@Test
	@Transactional
	public void testBasicParameterizedFind() {
		UserModel mhUser = UserModel
		    .builder()
		    .username("mhawk")
		    .fullName("Mike Hawk")
		    .birthDate(Calendar.getInstance())
		    .build();
		em.persist(mhUser);
		em.flush();
		em.clear();

		assertTrue(users.find(UserModel.class).build().setMaxResults(1).getSingleResult().equals(mhUser));
	}

	@Test
	@Transactional
	public void testSortBy() {
		UserModel it = UserModel.builder()
		    .username("pfile")
		    .fullName("Peter File")
		    .birthDate(Calendar.getInstance())
		    .build();
		em.persist(it);

		UserModel wf = UserModel.builder()
		    .username("wfeltersnatch")
		    .fullName("Wenton Feltersnatch")
		    .birthDate(Calendar.getInstance())
		    .build();
		em.persist(wf);

		UserModel hb = UserModel.builder()
		    .username("hbush")
		    .fullName("Harry Bush")
		    .birthDate(Calendar.getInstance())
		    .build();
		em.persist(hb);

		em.flush();

		List<UserModel> foundUsersAsc = users.find().sortBy(UserModel_.fullName).ascending().build().getResultList();
		assertTrue(hb.equals(foundUsersAsc.get(0)));
		assertTrue(it.equals(foundUsersAsc.get(1)));
		assertTrue(wf.equals(foundUsersAsc.get(2)));

		//List<UserModel> foundUserCustDesc = users.find().sortBy().fullName().descending().build().getResultList();
		
		List<UserModel> foundUsersDesc = users.find().sortBy(UserModel_.fullName).descending().build().getResultList();
		assertTrue(wf.equals(foundUsersDesc.get(0)));
		assertTrue(it.equals(foundUsersDesc.get(1)));
		assertTrue(hb.equals(foundUsersDesc.get(2)));
	}
}

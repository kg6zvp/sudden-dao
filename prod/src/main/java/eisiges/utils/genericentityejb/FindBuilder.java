package eisiges.utils.genericentityejb;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class FindBuilder<T> {
	protected Class<T> cArg;
	protected EntityManager em;

	protected CriteriaBuilder criteriaBuilder;
	protected CriteriaQuery<T> criteriaQuery;
	protected Root<T> queryRoot;

	public FindBuilder(Class<T> cArg, EntityManager em) {
		this.cArg = cArg;
		this.em = em;

		this.criteriaBuilder = this.em.getCriteriaBuilder();
		this.criteriaQuery = this.criteriaBuilder.createQuery(cArg);
		this.queryRoot = this.criteriaQuery.from(cArg);
	}

	public TypedQuery build() {
		return this.em.createQuery(criteriaQuery);
	}
}
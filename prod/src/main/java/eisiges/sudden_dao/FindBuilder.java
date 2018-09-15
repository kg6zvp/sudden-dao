package eisiges.sudden_dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

/**
 * A class building a find query
 * @author kg6zvp
 * @param <T> The type of Entity being found
 */
public class FindBuilder<T> {
	protected Class<T> cArg;
	protected EntityManager em;

	protected CriteriaBuilder criteriaBuilder;
	protected CriteriaQuery<T> criteriaQuery;
	protected Root<T> queryRoot;

	protected FindBuilder(Class<T> cArg, EntityManager em) {
		this.cArg = cArg;
		this.em = em;

		this.criteriaBuilder = this.em.getCriteriaBuilder();
		this.criteriaQuery = this.criteriaBuilder.createQuery(cArg);
		this.queryRoot = this.criteriaQuery.from(cArg);
	}

	/**
	 * Sort the results of the find query by a given attribute
	 * @param attribute the attribute to sort by
	 * return a {@link SortingBuilder} instance for the given attribute
	 */
	public <Y> SortingBuilder<FindBuilder<T>, Y> sortBy(final SingularAttribute<? super T, Y> attribute) {
		final FindBuilder<T> selfReference = this;
		return new SortingBuilder<FindBuilder<T>, Y>() {
			@Override
			public FindBuilder<T> ascending() {
				criteriaQuery.orderBy(criteriaBuilder.asc(queryRoot.get(attribute)));
				return selfReference;
			}
			@Override
			public FindBuilder<T> descending() {
				criteriaQuery.orderBy(criteriaBuilder.desc(queryRoot.get(attribute)));
				return selfReference;
			}
		};
	}

	/**
	 * Build a query that can be run
	 * @return a {@link TypedQuery}
	 */
	public TypedQuery<T> build() {
		return this.em.createQuery(criteriaQuery);
	}
}
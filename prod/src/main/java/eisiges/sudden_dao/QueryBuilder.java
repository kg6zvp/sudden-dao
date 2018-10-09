package eisiges.sudden_dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class QueryBuilder<T> {

	protected Class<T> cArg;
	protected EntityManager em;
	protected StringBuilder query;

	public QueryBuilder(Class<T> cArg, EntityManager em, String baseQuery) {
		this.cArg = cArg;
		this.em = em;
		this.query = new StringBuilder(baseQuery);
	}

	public TypedQuery<T> build() {
		return em.createQuery(query.toString(), cArg);
	}
}
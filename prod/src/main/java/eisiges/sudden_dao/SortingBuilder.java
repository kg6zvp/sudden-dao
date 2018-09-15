package eisiges.sudden_dao;

/**
 * builds a sort
 * @param <T> This is the class of the query builder
 * @param <Y> This is the type of the column the sort is based on
 */
public interface SortingBuilder<T, Y> {
	/**
	 * sort ascending
	 * @return a query builder with the sort applied
	 */
	T ascending();
	/**
	 * sort descending
	 * @return a query builder with the sort applied
	 */
	T descending();
}
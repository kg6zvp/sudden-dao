package eisiges.utils.genericentityejb;

/**
 * builds a sort
 * @param <T> This is the class of the entity in the query result
 * @param <Y> This is the type of the column the sort is based on
 */
public interface SortingBuilder<T, Y> {
	/**
	 * sort ascending
	 * @return a {@link FindBuilder} with the sort applied
	 */
	FindBuilder<T> ascending();
	/**
	 * sort descending
	 * @return a {@link FindBuilder} with the sort applied
	 */
	FindBuilder<T> descending();
}
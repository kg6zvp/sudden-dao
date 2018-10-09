package eisiges.sudden_dao.criteria;

/**
 * @author kg6zvp
 */
public interface ClauseBuilder<T, Y> {
	FindBuilder<T> notEqual(Y value);
	default FindBuilder<T> ne(Y value) {
		return notEqual(value);
	}
	FindBuilder<T> equal(Y value);
	default FindBuilder<T> eq(Y value) {
		return equal(value);
	}
	FindBuilder<T> greaterThan(Y value);
	default FindBuilder<T> gt(Y value) {
		return greaterThan(value);
	}
	FindBuilder<T> lessThan(Y value);
	default FindBuilder<T> lt(Y value) {
		return lessThan(value);
	}
	FindBuilder<T> isNull();
	FindBuilder<T> isNotNull();
}

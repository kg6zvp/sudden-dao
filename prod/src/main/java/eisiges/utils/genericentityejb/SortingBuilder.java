package eisiges.utils.genericentityejb;

public interface SortingBuilder<T, Y> {
	FindBuilder<T> ascending();
	FindBuilder<T> descending();
}
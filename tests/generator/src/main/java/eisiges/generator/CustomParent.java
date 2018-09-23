package eisiges.generator;

import eisiges.sudden_dao.GenericPersistenceManager;

public class CustomParent<T, K> extends GenericPersistenceManager<T, K> {
	public CustomParent(Class<T> cArg) {
		super(cArg);
	}

	public String customMethod() {
		return "Custom Method Result";
	}
}
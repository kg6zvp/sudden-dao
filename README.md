# GenericEntityEJB
EJB abstraction for EntityManager in JPA 2 environments

Manually composing JPQL queries is often annoying when we want to spend more time focusing on the business logic of an application. To that end, I created this simple project.

Use Cases:

	1.) I want to query my database for all objects whose values match my object's non-null object member variables and get back a list. (Writing this kind of query for every single entity by hand is annoying)

		To search for a Rock variable with it's color variable set to BLUE where my EJB which extends GenericEntityEJB is rockManager:

			Rock keyRock = new Rock();

			keyRock.setColor(Color.BLUE);

			List<Rock> blueRocks = rockManager.getMatching(keyRock); //this assumes any other member variables in this class are null

	2.) I want to retrieve an object's key //not as sure why you might want this

		rockManager.getId(myObject);

Getting started:

	1.) Create a new EJB which you would normally inject an EntityManager into which subclasses GenericPersistenceManager<T, K> (where T is the Type of class it will manage and K is the Type of your Entity's @Id field)

	2.) Create a new public constructor in your EJB with a call to super() which passes in your Entity's Class:

	3.) (optional) Typically I mark such classes as @Local and @Stateless and inject them when needed, ending up like this:

		@Local (in case you've never seen this before, the import statement needs javax.ejb.Local)

		@Stateless (Likewise, this is javax.ejb.Stateless)

		public class UserBean extends GenericPersistenceManager<MyUser, Long> {

			public UserBean(){

				super(MyUser.class);

			}

		}

The following functions are exposed by the GenericEntityEJB:

	boolean containsKey(K)

	T persist(T)

	K getId(T)

	T save(T)

	T get(K)

	void remove(T)

	void saveAll(List<T>)

	void removeAll(List<T>)

	List<T> getAll()

	boolean isTableEmpty()

	List<T> getMatching(T)
	

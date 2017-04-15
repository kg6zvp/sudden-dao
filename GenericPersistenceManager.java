package enterprises.mccollum.utils.genericentityejb;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Intended to be subclassed for use as a persistence manager. It contains all boilerplate code for a class used to manage persistent Entities.
 * 
 * Usage:
 *	Extend this persistence manager class and annotate your bean as {@link Local} and {@link Stateless}
 *	Create a default constructor with a single call to super(Class.class); where Class is the name of the {@link Entity} you wish to manage
 * @author Sam McCollum
 * 
 * @param <T> The type of Object being managed
 * @param <K> The type used for the Entity's {@link Id}
 */
public class GenericPersistenceManager<T, K> {
	Class<T> cArg;
	String tableName;
	
	@PersistenceContext
	protected EntityManager em;
	
	public GenericPersistenceManager(Class<T> cArg, String tableName){
		this.cArg = cArg;
		this.tableName = tableName;
	}
	
	public GenericPersistenceManager(Class<T> cArg){
		this(cArg, cArg.getSimpleName());
	}
	
	/**
	 * Check whether or not an object with the given key (id) is in the database
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key){
		if(key == null)
			return false;
		return (get(key) != null);
	}
	
	/**
	 * Persist and return the object
	 * @param data
	 * @return
	 */
	public T persist(T data){
		em.persist(data);
		em.flush();
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public K getId(T data) {
		return (K)em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(data);
	}

	/**
	 * Save an object to the database
	 * @param data
	 * @return
	 */
	public T save(T data){
		return em.merge(data);
	}
	/**
	 * Retrieve an object from the database with the given key (id)
	 * @param key
	 * @return
	 */
	public T get(K key){
		return em.find(cArg, key);
	}
	/**
	 * Remove an object from the database
	 * @param data
	 */
	public void remove(T data){
		em.remove(em.merge(data));
	}
	/**
	 * Save a list of objects to the database
	 * @param data
	 */
	public void saveAll(List<T> data){
		for(T d : data)
			save(d);
	}
	/**
	 * Remove a list of objects from the database
	 * @param data
	 */
	public void removeAll(List<T> data){
		for(T d : data)
			remove(d);
	}
	/**
	 * Retrieve a list of {@link T}, all of the ones in the database
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> getAll(){
		Query q = getSelectAllQuery();
		return (List<T>)q.getResultList();
	}
	
	/**
	 * Checks whether or not the table is empty
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isTableEmpty(){
		List<T> results = getSelectAllQuery().setMaxResults(2).getResultList();
		return (results.size() < 1);
	}
	/**
	 * private helper method
	 * @return
	 */
	private Query getSelectAllQuery(){
		return em.createQuery(getSelectAllQueryString());
	}
	/**
	 * private helper method
	 * @return
	 */
	private String getSelectAllQueryString(){
		return "SELECT data FROM "+tableName+" data";
	}
	
	/**
	 * Retrieve a class from the database whose non-null instance variables match those of the class in the database
	 * @param keyObject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> getMatching(T keyObject){
		Field[] members = cArg.getDeclaredFields();
		Map<String, Object> importantFields = new TreeMap<>();
		for(Field member : members){
			try {
				member.setAccessible(true);
				if(!member.getType().isPrimitive()){
					Object val = member.get(keyObject);
					if(val != null && !Modifier.isFinal(member.getModifiers()) && !Modifier.isStatic(member.getModifiers()) && !(val instanceof Collection))
						importantFields.put(member.getName(), val);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		String queryString = getSelectAllQueryString();
		int run = 0;
		for(Map.Entry<String, Object> field : importantFields.entrySet()){
			if(run == 0){
				queryString += String.format(" where data.%s = :%s", field.getKey(), field.getKey());
			}else{
				queryString += String.format(" and data.%s = :%s", field.getKey(), field.getKey());
			}
			++run;
		}
		System.out.printf("Composed query string:\n%s\n", queryString);
		Query q = em.createQuery(queryString);
		for(Map.Entry<String, Object> field : importantFields.entrySet()){
			q.setParameter(field.getKey(), field.getValue());
		}
		return q.getResultList();
	}
	
	private List<Field> getNonNullFields(T keyObject){
		Field[] members = cArg.getDeclaredFields();
		List<Field> importantFields = new LinkedList<>();
		for(Field member : members){
			try {
				member.setAccessible(true);
				if(!member.getType().isPrimitive()){
					Object val = member.get(keyObject);
					if(val != null)
						importantFields.add(member);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return importantFields;
	}
	
	public List<Object> getPropertyList(T keyObject){
		List list = new LinkedList();
		List<Field> importantFields = getNonNullFields(keyObject);
		if(importantFields.size() > 0){
			Field fiq = importantFields.get(0);
			fiq.setAccessible(true);
			for(T data : getAll()){
				try {
					list.add(fiq.get(data));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}

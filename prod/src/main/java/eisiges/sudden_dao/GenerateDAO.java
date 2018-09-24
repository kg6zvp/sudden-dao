package eisiges.sudden_dao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kg6zvp
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateDAO {
	public String daoName() default "";
	public Class<? extends GenericPersistenceManager> parentClass() default GenericPersistenceManager.class;
}

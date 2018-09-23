package eisiges.generator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eisiges.sudden_dao.GenerateDAO;

@Entity
@GenerateDAO(parentClass = CustomParent.class)
public class CustomEntity {
	@Id
	@GeneratedValue
	public Long id;

	public String nameOfEntity;
}
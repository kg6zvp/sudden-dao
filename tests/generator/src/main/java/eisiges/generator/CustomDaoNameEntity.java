package eisiges.generator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eisiges.sudden_dao.GenerateDAO;

@Entity
@GenerateDAO(daoName = "MyCustomDao")
public class CustomDaoNameEntity {
	@Id
	@GeneratedValue
	public Long id;

	public String hasCustomDao;
	public String someValue;
}
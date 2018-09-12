package eisiges.find;

import eisiges.sudden_dao.GenericPersistenceManager;

public class UserGEM extends GenericPersistenceManager<UserModel, Long> {
	public UserGEM(){
		super(UserModel.class);
	}
}
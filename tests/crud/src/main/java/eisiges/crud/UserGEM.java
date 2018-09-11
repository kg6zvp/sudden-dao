package eisiges.crud;

import eisiges.utils.sudden_dao.GenericPersistenceManager;

public class UserGEM extends GenericPersistenceManager<UserModel, Long> {
	public UserGEM(){
		super(UserModel.class);
	}
}
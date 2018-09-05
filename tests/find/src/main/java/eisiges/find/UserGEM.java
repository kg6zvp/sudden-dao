package eisiges.find;

import eisiges.utils.genericentityejb.GenericPersistenceManager;

public class UserGEM extends GenericPersistenceManager<UserModel, Long> {
	public UserGEM(){
		super(UserModel.class);
	}
}
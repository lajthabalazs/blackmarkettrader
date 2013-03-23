package hu.edudroid.blackmarkettmit.server;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import hu.edudroid.blackmarkettmit.server.persistence.BlackMarketUser;

public class UserManager {
	public static BlackMarketUser getCurrentUser(PersistenceManager pm) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		return getCurrentUser(pm, user);
	}

	public static BlackMarketUser getCurrentUser(PersistenceManager pm, User user) {
		Query q = pm.newQuery(BlackMarketUser.class);
		q.setFilter("externalId == '" + user.getEmail() + "'");
		@SuppressWarnings("unchecked")
		List<BlackMarketUser> result = (List<BlackMarketUser>) q.execute();
		if (result.size() == 0){
			return null;
		} else {
			return result.get(0);
		}
	}
}

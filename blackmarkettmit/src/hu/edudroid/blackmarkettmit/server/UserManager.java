package hu.edudroid.blackmarkettmit.server;

import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserManager {
	public static BlackMarketUser getCurrentUser() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user!=null) {
			return BlackMarketUserUtils.getUserByExternalId(user.getEmail());
		} else {
			return null;
		}
	}
}

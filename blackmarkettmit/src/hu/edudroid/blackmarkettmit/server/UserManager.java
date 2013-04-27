package hu.edudroid.blackmarkettmit.server;

import javax.servlet.http.HttpSession;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;

public class UserManager {
	public static BlackMarketUser getCurrentUser(HttpSession session) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();	
		System.out.println("Checking user " + user);
		if (user != null) {
			System.out.println("Google user found.");
			return BlackMarketUserUtils.getUserByExternalId(user.getUserId());
		}
		String userId = (String) session.getAttribute(FacebookServlet.USER_ID);
		if (userId != null) {
			System.out.println("Facebook user found.");
			return BlackMarketUserUtils.getUserByExternalId(userId);
		}
		System.out.println("Facebook user found.");
		return null;
	}
}
package hu.edudroid.blackmarkettmit.server;

import javax.servlet.http.HttpSession;

import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;

public class UserManager {
	public static BlackMarketUser getCurrentUser(HttpSession session) {
		String userId = (String) session.getAttribute(FacebookServlet.USER_ID);
		if (userId != null) {
			return BlackMarketUserUtils.getUserByExternalId(userId);
		} else {
			return null;
		}
	}
}

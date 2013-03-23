package hu.edudroid.blackmarkettmit.server;

import hu.edudroid.blackmarkettmit.client.services.LoginService;
import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;
import hu.edudroid.blackmarkettmit.shared.LoginInfo;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	private static final long serialVersionUID = -8138238825148479161L;

	public LoginInfo login(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		LoginInfo loginInfo = new LoginInfo();

		if (user != null) {
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
			// Check if user is part of the system
			
			BlackMarketUser blackMarketUser = BlackMarketUserUtils.getUserByEmail(user.getEmail());
			if (blackMarketUser == null) {
				blackMarketUser = new BlackMarketUser();
				blackMarketUser.setExternalId(loginInfo.getEmailAddress());
				blackMarketUser.setRandom((float)Math.random());
				BlackMarketUserUtils.save(blackMarketUser);
			}
			loginInfo.setBlackMarketUser(blackMarketUser);
		} else {
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return loginInfo;
	}

}
package hu.edudroid.blackmarkettmit.server;

import javax.servlet.http.HttpSession;

import hu.edudroid.blackmarkettmit.client.services.LoginService;
import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;
import hu.edudroid.blackmarkettmit.shared.LoginInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	private static final long serialVersionUID = -8138238825148479161L;

	public LoginInfo login(String requestUri) {
		HttpSession session = getThreadLocalRequest().getSession();
		String userId = (String) session.getAttribute(FacebookServlet.USER_ID);
		String userName = (String) session.getAttribute(FacebookServlet.USER_NAME);
		LoginInfo loginInfo = new LoginInfo();

		if (userId != null) {
			loginInfo.setLoggedIn(true);
			loginInfo.setExternalId(userId);
			loginInfo.setNickname(userName);
			// Check if user is part of the system			
			BlackMarketUser blackMarketUser = BlackMarketUserUtils.getUserByExternalId(userId);
			if (blackMarketUser == null) {
				blackMarketUser = new BlackMarketUser();
				blackMarketUser.setExternalId(userId);
				blackMarketUser.setRandom((float)Math.random());
				BlackMarketUserUtils.save(blackMarketUser);
			}
			loginInfo.setBlackMarketUser(blackMarketUser);
		} else {
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl("/FacebookServlet");
		}
		return loginInfo;
	}

}
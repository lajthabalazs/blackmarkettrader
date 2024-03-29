package hu.edudroid.blackmarkettmit.server;

import java.util.Date;

import hu.edudroid.blackmarkettmit.client.services.LoginService;
import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;
import hu.edudroid.blackmarkettmit.shared.LoginInfo;

import javax.servlet.http.HttpSession;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	private static final long serialVersionUID = -8138238825148479161L;

	public LoginInfo checkIfLoggedIn(String requestUri) {
		System.out.println("LoginServiceImpl.checkIfLoggedIn");
		// Check if logged in with google
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();		
		// Check if logged in with facebook
		HttpSession session = getThreadLocalRequest().getSession();
		String userId = (String) session.getAttribute(FacebookServlet.USER_ID);
		String userName = (String) session.getAttribute(FacebookServlet.USER_NAME);
		UserManager.getCurrentUser(session);
		LoginInfo loginInfo = new LoginInfo();
		String gender = BlackMarketUser.GENDER_UNKNOWN;
		String birthday = null;
		String emailAddress = "";
		
		if ((user != null)||(userId != null)) {
			if (user != null) {
				System.out.println("Found google userid");
				session.removeAttribute(FacebookServlet.USER_ID);
				session.removeAttribute(FacebookServlet.USER_NAME);
				userId = user.getUserId();
				emailAddress = user.getEmail();
				loginInfo.setLoggedIn(true);
				loginInfo.setExternalId(user.getUserId());
				loginInfo.setNickname(user.getNickname());
				loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
				// Check if user is part of the system			
			} else if (userId != null) {
				System.out.println("Found userid in session");
				loginInfo.setLoggedIn(true);
				loginInfo.setExternalId(userId);
				loginInfo.setNickname(userName);
				loginInfo.setLogoutUrl("/FacebookLogoutServlet");
				gender = (String) session.getAttribute("gender");
				birthday = (String) session.getAttribute("birthday");
				emailAddress = (String) session.getAttribute("emailAddress");
			}
			// Check if user is part of the system			
			BlackMarketUser blackMarketUser = BlackMarketUserUtils.getUserByExternalId(userId);
			if (blackMarketUser == null) {
				System.out.println("New user");
				blackMarketUser = new BlackMarketUser();
				blackMarketUser.setExternalId(loginInfo.getExternalId());
				blackMarketUser.setUserName(loginInfo.getNickname());
				blackMarketUser.setRandom((float)Math.random());
				blackMarketUser.setEmailAddress(emailAddress);
				blackMarketUser.setGender(gender);
				blackMarketUser.setBirthday(birthday);
			} else {
				System.out.println("User in database");				
			}
			BlackMarketUserUtils.addLoginEvent(blackMarketUser);
			BlackMarketUserUtils.save(blackMarketUser);

			loginInfo.setBlackMarketUser(blackMarketUser);
		} else {
			System.out.println("Not logged in");				
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginWithFacebookUrl("/FacebookServlet");
			loginInfo.setLoginWithGoogleUrl(userService.createLoginURL(requestUri));
		}
		loginInfo.setServerTime(System.currentTimeMillis());
		return loginInfo;

	}

	@Override
	public boolean notificationsDisplayed(boolean rewardsShown, boolean notificationsShown, boolean tutorialsShown) {
		System.out.println("Saving notifications. Reward " + rewardsShown + " notifications " + notificationsShown + " tutorials " + tutorialsShown);
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser(getThreadLocalRequest().getSession());
		if (blackMarketUser != null) {
			long date = new Date().getTime();
			if (rewardsShown) {
				blackMarketUser.setLastRewardView(date);
			}
			if (notificationsShown) {
				blackMarketUser.setLastNotificationView(date);
			}
			if (tutorialsShown) {
				blackMarketUser.setLastTutorialView(date);
			}
			BlackMarketUserUtils.save(blackMarketUser);
			return true;
		} else {
			return false;
		}
	}
}
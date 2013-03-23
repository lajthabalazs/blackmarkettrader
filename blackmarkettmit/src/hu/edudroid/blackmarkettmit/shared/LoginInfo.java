package hu.edudroid.blackmarkettmit.shared;

import java.io.Serializable;

public class LoginInfo implements Serializable {

	private static final long serialVersionUID = 8010763846801676585L;
	private boolean loggedIn = false;
	private String loginUrl;
	private String logoutUrl;
	private String emailAddress;
	private String nickname;
	private BlackMarketUser blackMarketUser;

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public BlackMarketUser getBlackMarketUser() {
		return blackMarketUser;
	}

	public void setBlackMarketUser(BlackMarketUser blackMarketUser) {
		this.blackMarketUser = blackMarketUser;
	}
}
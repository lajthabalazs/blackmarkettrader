package hu.edudroid.blackmarkettmit.shared;

import java.io.Serializable;

public class LoginInfo implements Serializable {

	private static final long serialVersionUID = 8010763846801676585L;
	private boolean loggedIn = false;
	private String loginWithFacebookUrl;
	private String loginWithGoogleUrl;
	private String externalId;
	private String nickname;
	private BlackMarketUser blackMarketUser;
	private String logoutUrl;
	private long serverTime;

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginWithFacebookUrl() {
		return loginWithFacebookUrl;
	}

	public void setLoginWithFacebookUrl(String loginWithFacebookUrl) {
		this.loginWithFacebookUrl = loginWithFacebookUrl;
	}

	public String getLoginWithGoogleUrl() {
		return loginWithGoogleUrl;
	}

	public void setLoginWithGoogleUrl(String loginWithGoogleUrl) {
		this.loginWithGoogleUrl = loginWithGoogleUrl;
	}
	
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
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

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}
}
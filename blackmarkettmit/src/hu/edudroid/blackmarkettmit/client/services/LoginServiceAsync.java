package hu.edudroid.blackmarkettmit.client.services;

import hu.edudroid.blackmarkettmit.shared.LoginInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	void checkIfLoggedIn(String requestUri, AsyncCallback<LoginInfo> callback);

	void notificationsDisplayed(boolean rewardsShown, boolean notificationsShown, boolean tutorialsShown, AsyncCallback<Boolean> callback);
}

package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.LoginInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	void login(String requestUri, AsyncCallback<LoginInfo> callback);
}

package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContactListServiceAsync {

	void getPlayerIds(AsyncCallback<Contact[]> callback);
	void requestRandomContact(AsyncCallback<Void> callback);

}

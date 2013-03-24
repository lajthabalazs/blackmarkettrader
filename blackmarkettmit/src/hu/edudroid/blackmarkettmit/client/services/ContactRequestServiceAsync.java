package hu.edudroid.blackmarkettmit.client.services;

import hu.edudroid.blackmarkettmit.shared.Contact;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContactRequestServiceAsync {


	void askForRecommandation(String otherPlayerId, AsyncCallback<Void> callback);
	void getContacts(AsyncCallback<List<Contact>> callback);
	void newRandomContact(AsyncCallback<List<Contact>> callback);
	void play(String otherPlayerId, int choice, AsyncCallback<Integer> callback);
}

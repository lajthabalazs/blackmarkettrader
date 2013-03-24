package hu.edudroid.blackmarkettmit.client.services;

import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.RecommandationRequest;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContactRequestServiceAsync {

	void getRecommandationRequests(AsyncCallback<List<RecommandationRequest>> callback);
	void getContacts(AsyncCallback<List<Contact>> callback);
	void newRandomContact(AsyncCallback<List<Contact>> callback);
	void play(String playerId, String otherPlayerId, int choice, AsyncCallback<Integer> callback);
}

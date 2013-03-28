package hu.edudroid.blackmarkettmit.client.services;

import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.Tupple;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContactRequestServiceAsync {


	void askForRecommandation(String otherPlayerId, AsyncCallback<Void> callback);
	void getContacts(AsyncCallback<List<Contact>> callback);
	void newRandomContact(AsyncCallback<Tupple<Contact,List<Contact>>> callback);
	void play(String otherPlayerId, int choice, AsyncCallback<Tupple<Integer, List<Contact>>> callback);
	void getAlligibleContacts(String otherPlayerId,
			AsyncCallback<List<Contact>> callback);
	void suggestContact(String otherPlayerId, String suggestedPlayerId,
			AsyncCallback<Void> callback);
}
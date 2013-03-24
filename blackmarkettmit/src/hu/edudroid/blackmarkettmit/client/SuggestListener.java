package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

public interface SuggestListener {

	void userWantsToSuggestForContact(Contact contact);

	void userSuggestedContactForContact(Contact contact, Contact suggestedContact);

}

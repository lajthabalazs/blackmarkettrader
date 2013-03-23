package hu.edudroid.blackmarkettmit.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hu.edudroid.blackmarkettmit.client.NotLoggedInException;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestService;
import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;
import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.RecommandationRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ContactRequestServiceImpl  extends RemoteServiceServlet implements ContactRequestService{

	private static final long serialVersionUID = 2355080806520903322L;

	@Override
	public List<RecommandationRequest> getRecommandationRequests() throws NotLoggedInException{
		List<RecommandationRequest> recommandations = new ArrayList<RecommandationRequest>();
		for (int i = 0; i < 20; i++) {
			RecommandationRequest recommandation = new RecommandationRequest();
			recommandation.setText("Recommandation " + i);
			recommandation.setRequestDate(new Date());
			recommandation.setAnswered(false);
			recommandations.add(recommandation);
		}
		return recommandations;
	}

	@Override
	public List<Contact> newRandomContact() throws NotLoggedInException {
		BlackMarketUser user = UserManager.getCurrentUser();
		if (user == null) {
			return null;
		}
		List<Contact> contacts = ContactUtils.getContactsForUser(user.getUserKey());
		BlackMarketUser possibleContact = null;
		System.out.println("Random contact for " + user.getUserKey());
		tries:for (int i = 0; i < 30; i++) {
			possibleContact = BlackMarketUserUtils.getRandomUser();
			if (possibleContact == null) {
				System.out.println("Contact not found.");
				continue tries;
			}
			if (possibleContact.getUserKey().equals(user.getUserKey())) {
				System.out.println("Contact self " + possibleContact.getUserKey());
				possibleContact = null;
				continue tries;
			}
			for (Contact contact : contacts) {
				if (contact.getFirstPlayerKey().equals(possibleContact.getUserKey())
						|| contact.getSecondPlayerKey().equals(possibleContact.getUserKey())) {
					possibleContact = null;
					continue tries;
				}
				// We have an eligible contact
				System.out.println("Contact found " + possibleContact.getUserKey());
				break tries;
			}
		}
		if (possibleContact == null) {
			// Couldn't find contact
			return null;
		} else {
			// Create contact from user and found possible contact
			Contact newContact = ContactUtils.createContact(user, possibleContact);
			ContactUtils.save(newContact);
			// Add to contact list
			ArrayList<Contact> rets = new ArrayList<Contact>();
			rets.add(newContact);
			return rets;
		}
	}

	@Override
	public List<Contact> getContacts() throws NotLoggedInException {
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser();
		return ContactUtils.getContactsForUser(blackMarketUser.getUserKey());
	}
}
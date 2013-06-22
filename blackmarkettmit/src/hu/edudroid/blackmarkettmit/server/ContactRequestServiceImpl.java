package hu.edudroid.blackmarkettmit.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import hu.edudroid.blackmarkettmit.client.NotLoggedInException;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestService;
import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;
import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.Tupple;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ContactRequestServiceImpl  extends RemoteServiceServlet implements ContactRequestService{

	private static final long serialVersionUID = 2355080806520903322L;

	@Override
	public Tupple<Contact, List<Contact>> newRandomContact() throws NotLoggedInException {
		System.out.println("ContactRequestServiceImpl.newRandomContact");
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser(getThreadLocalRequest().getSession());
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		BlackMarketUserUtils.userRequestOccured(blackMarketUser);
		List<Contact> contacts = ContactUtils.getContactsForUser(blackMarketUser.getEntityKey());
		BlackMarketUser possibleContact = null;
		tries:for (int i = 0; i < 30; i++) {
			possibleContact = BlackMarketUserUtils.getRandomUser();
			if (possibleContact == null) {
				continue tries;
			}
			if (possibleContact.getEntityKey().equals(blackMarketUser.getEntityKey())) {
				possibleContact = null;
				continue tries;
			}
			Contact contact = ContactUtils.getContactForUsers(blackMarketUser.getEntityKey(), possibleContact.getEntityKey());
			if (contact != null) {
				possibleContact = null;
			} else {
				// We have an eligible contact
				break tries;
			}
		}
		if (possibleContact == null) {
			// Couldn't find contact
			System.out.println("ContactRequestServiceImpl.newRandomContact no contact");
			return new Tupple<Contact, List<Contact>>(null, contacts);
		} else {
			// Create contact from user and found possible contact
			Contact newContact = ContactUtils.createContact(blackMarketUser, possibleContact);
			newContact.setWhoRequested(0);
			newContact.setRequestDate(new Date());
			ContactUtils.save(newContact);
			// Add to contact list
			contacts.add(newContact);
			System.out.println("ContactRequestServiceImpl.newRandomContact new contact found");
			return new Tupple<Contact, List<Contact>>(newContact, contacts);
		}
	}

	@Override
	public List<Contact> getContacts() throws NotLoggedInException {
		System.out.println("ContactRequestServiceImpl.getContacts");
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser(getThreadLocalRequest().getSession());
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		BlackMarketUserUtils.userRequestOccured(blackMarketUser);
		List<Contact> contacts = ContactUtils.getContactsForUser(blackMarketUser.getEntityKey());
		System.out.println("ContactRequestServiceImpl.getContacts return " + contacts.size());
		return contacts;
	}

	@Override
	public Tupple<Integer, List<Contact>> play(String otherPlayerId, int choice) throws NotLoggedInException {
		System.out.println("ContactRequestServiceImpl.play");
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser(getThreadLocalRequest().getSession());
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		BlackMarketUserUtils.userRequestOccured(blackMarketUser);
		String playerId = blackMarketUser.getEntityKey();
		//Contact contact = ContactUtils.getContactForUsers(playerId, otherPlayerId);
		Contact contact = null;
		List<Contact> contacts = ContactUtils.getContactsForUser(playerId);
		for (Contact listedContact : contacts) {
			if (listedContact.getViewer() == 0) {
				if (listedContact.getSecondPlayerKey().equals(otherPlayerId)){
					contact = listedContact;
				}
			} else {
				if (listedContact.getFirstPlayerKey().equals(otherPlayerId)){
					contact = listedContact;
				}
			}
		}
		if (contact == null) {
			System.out.println("ContactRequestServiceImpl.play");
			return new Tupple<Integer, List<Contact>>(PLAY_RESULT_DECLINED, contacts);
		}
		if ((choice != Contact.CHOICE_COOPERATE) && (choice != Contact.CHOICE_DEFECT) && (choice != Contact.CHOICE_REJECT)) {
			System.out.println("ContactRequestServiceImpl.play declined: not a proper option");
			return new Tupple<Integer, List<Contact>>(PLAY_RESULT_DECLINED, contacts);
		}
		int player = contact.getViewer();
		// Process request
		if (contact.isLastTradeClosed()) {			
			// New game
			// Can't reject when no game
			if (choice == Contact.CHOICE_REJECT) {
				System.out.println("ContactRequestServiceImpl.play declined: can't reject if not in game");
				return new Tupple<Integer, List<Contact>>(PLAY_RESULT_DECLINED, contacts);
			}
			System.out.println("ContactRequestServiceImpl.play storing choice");
			ContactUtils.addEvent(contact, player,(choice == Contact.CHOICE_COOPERATE?Contact.HISTORY_INVITE_AND_COOP:Contact.HISTORY_INVITE_AND_DEFECT));
			ContactUtils.save(contact);
			return new Tupple<Integer, List<Contact>>(PLAY_RESULT_ACCEPTED, contacts);
		} else {
			// Old game
			// User initiated, user can't change his choice
			if (contact.getWhoStarted() == contact.getViewer()) {
				System.out.println("ContactRequestServiceImpl.play declined: player was the initiator");
				return new Tupple<Integer, List<Contact>>(PLAY_RESULT_DECLINED, contacts);
			}
			// User responds
			// Save result
			switch (choice) {
			 case Contact.CHOICE_COOPERATE:
				 ContactUtils.addEvent(contact, player, Contact.HISTORY_ACCEPT_AND_COOP);
				 break;
			 case Contact.CHOICE_DEFECT:
				 ContactUtils.addEvent(contact, player, Contact.HISTORY_ACCEPT_AND_DEFECT);
				 break;
			 case Contact.CHOICE_REJECT:
				 ContactUtils.addEvent(contact, player, Contact.HISTORY_REJECT);
				 break;
			}
			ContactUtils.save(contact);
			System.out.println("ContactRequestServiceImpl.play saved user choice");
			return new Tupple<Integer, List<Contact>>(PLAY_RESULT_ACCEPTED, contacts);
		}
	}

	@Override
	public void askForRecommandation(String otherPlayerId) throws NotLoggedInException {
		System.out.println("ContactRequestServiceImpl.askForRecommandation");
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser(getThreadLocalRequest().getSession());
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		BlackMarketUserUtils.userRequestOccured(blackMarketUser);
		String playerId = blackMarketUser.getEntityKey();
		Contact contact = ContactUtils.getContactForUsers(playerId, otherPlayerId);
		if (contact.getViewer()==0) {
			contact.setFirstPlayerRequestsRecommandation(new Date());
		} else {
			contact.setSecondPlayerRequestsRecommandation(new Date());
		}
		ContactUtils.save(contact);
	}

	@Override
	public List<Contact> getAlligibleContacts(String otherPlayerId) throws NotLoggedInException {
		System.out.println("ContactRequestServiceImpl.getAlligibleContacts");
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser(getThreadLocalRequest().getSession());
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		BlackMarketUserUtils.userRequestOccured(blackMarketUser);
		String playerId = blackMarketUser.getEntityKey();
		HashSet<String> alreadyContacts = new HashSet<String>();
		List<Contact> allegibleContacts = new ArrayList<Contact>();
		List<Contact> othersContacts = ContactUtils.getContactsForUser(otherPlayerId);
		List<Contact> contacts = ContactUtils.getContactsForUser(playerId);
		for (Contact contact : othersContacts) {
			if (contact.getViewer() == 0) {
				alreadyContacts.add(contact.getSecondPlayerKey());
			} else {
				alreadyContacts.add(contact.getFirstPlayerKey());
			}
		}
		alreadyContacts.add(playerId);
		alreadyContacts.add(otherPlayerId);
		for (Contact contact : contacts) {
			if (contact.getViewer() == 0) {
				if (!alreadyContacts.contains(contact.getSecondPlayerKey())) {
					allegibleContacts.add(contact);
				}
			} else {
				if (!alreadyContacts.contains(contact.getFirstPlayerKey())) {
					allegibleContacts.add(contact);
				}
			}
		}
		return allegibleContacts;
	}

	@Override
	public void suggestContact(String otherPlayerId, String suggestedPlayerId) throws NotLoggedInException {
		System.out.println("ContactRequestServiceImpl.suggestContact");
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser(getThreadLocalRequest().getSession());
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		BlackMarketUserUtils.userRequestOccured(blackMarketUser);
		String playerId = blackMarketUser.getEntityKey();
		Contact playerOtherPlayer = ContactUtils.getContactForUsers(playerId, otherPlayerId);
		Contact playerSuggestedPlayer = ContactUtils.getContactForUsers(playerId, suggestedPlayerId);
		Contact existingContact = ContactUtils.getContactForUsers(otherPlayerId, suggestedPlayerId);		
		if (playerOtherPlayer != null && playerSuggestedPlayer != null && existingContact == null) {
			BlackMarketUser otherUser = BlackMarketUserUtils.getUserByKey(otherPlayerId);
			BlackMarketUser suggestedUser = BlackMarketUserUtils.getUserByKey(suggestedPlayerId);
			Contact newContact = ContactUtils.createContact(otherUser, suggestedUser);
			newContact.setWhoRequested(0);
			if (playerOtherPlayer.getViewer() == 0) {
				newContact.setRequestDate(playerOtherPlayer.getSecondPlayerRequestsRecommandation());
				playerOtherPlayer.setSecondPlayerRequestsRecommandation(null);
			} else {
				newContact.setRequestDate(playerOtherPlayer.getFirstPlayerRequestsRecommandation());
				playerOtherPlayer.setFirstPlayerRequestsRecommandation(null);
			}
			newContact.setWhoSuggested(blackMarketUser.getEntityKey());
			ContactUtils.save(newContact);
			ContactUtils.save(playerOtherPlayer);
		}
	}	
}
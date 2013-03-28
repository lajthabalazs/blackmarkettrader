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
		BlackMarketUser user = UserManager.getCurrentUser();
		if (user == null) {
			throw new NotLoggedInException();
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
			System.out.println("Checking if in contact with " + possibleContact.getUserKey());
			Contact contact = ContactUtils.getContactForUsers(user.getUserKey(), possibleContact.getUserKey());
			if (contact != null) {
				possibleContact = null;
				System.out.println("Common contact found.");
			} else {
				// We have an eligible contact
				System.out.println("Possible contact found " + possibleContact.getUserKey());
				break tries;
			}
		}
		if (possibleContact == null) {
			// Couldn't find contact
			return new Tupple<Contact, List<Contact>>(null, contacts);
		} else {
			// Create contact from user and found possible contact
			Contact newContact = ContactUtils.createContact(user, possibleContact);
			ContactUtils.save(newContact);
			// Add to contact list
			contacts.add(newContact);
			return new Tupple<Contact, List<Contact>>(newContact, contacts);
		}
	}

	@Override
	public List<Contact> getContacts() throws NotLoggedInException {
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser();
		if (blackMarketUser != null) {
			return ContactUtils.getContactsForUser(blackMarketUser.getUserKey());
		} else {
			return null;
		}
	}

	@Override
	public Tupple<Integer, List<Contact>> play(String otherPlayerId, int choice) throws NotLoggedInException {
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser();
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		String playerId = blackMarketUser.getUserKey();
		System.out.println("Play");
		System.out.println("First:     " + playerId);
		System.out.println("Second:    " + otherPlayerId);
		System.out.println("Choice:    " + choice);
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
			System.out.println("No player.");
			return new Tupple<Integer, List<Contact>>(PLAY_RESULT_DECLINED, contacts);
		}
		System.out.println("Viewer:    " + contact.getViewer());
		if ((choice != Contact.CHOICE_COOPERATE) && (choice != Contact.CHOICE_DEFECT) && (choice != Contact.CHOICE_REJECT)) {
			return new Tupple<Integer, List<Contact>>(PLAY_RESULT_DECLINED, contacts);
		}
		if (choice == Contact.CHOICE_REJECT) {
			contact.setInGame(0);
			contact.setWhoStarted(-1);
			ContactUtils.save(contact);
			return new Tupple<Integer, List<Contact>>(PLAY_RESULT_ACCEPTED, contacts);
		}
		if (contact.getInGame() == 0) {
			// New game
			contact.setInGame(1);
			contact.setGameStart(new Date());
			int viewer = contact.getViewer();
			if (viewer == 0) {
				contact.setWhoStarted(0);
				contact.setFirstPlayerChoice(choice);
			} else {
				contact.setWhoStarted(1);
				contact.setSecondPlayerChoice(choice);
			}
			ContactUtils.save(contact);
			return new Tupple<Integer, List<Contact>>(PLAY_RESULT_ACCEPTED, contacts);
		} else {
			// Old game
			// User want's to change his choice
			if (contact.getWhoStarted() == contact.getViewer()) {
				return new Tupple<Integer, List<Contact>>(PLAY_RESULT_DECLINED, contacts);
			}
			// User responds
			contact.setInGame(0);
			int viewer = contact.getViewer();
			if (viewer == 0) {
				contact.setFirstPlayerChoice(choice);
			} else {
				contact.setSecondPlayerChoice(choice);
			}
			// Evaluate result
			contact.setGameCount(contact.getGameCount() + 1);
			if (contact.getFirstPlayerChoice() == Contact.CHOICE_COOPERATE) {
				if (contact.getSecondPlayerChoice() == Contact.CHOICE_COOPERATE) {
					contact.setCooperationCount(contact.getCooperationCount() + 1);
					ContactUtils.save(contact);
					return new Tupple<Integer, List<Contact>>(PLAY_RESULT_COOPERATE, contacts);
				}else {
					contact.setSecondDefectCount(contact.getSecondDefectCount() + 1);
					ContactUtils.save(contact);
					if (viewer == 0) {
						return new Tupple<Integer, List<Contact>>(PLAY_RESULT_HE_DEFECTED, contacts);
					} else {
						return new Tupple<Integer, List<Contact>>(PLAY_RESULT_I_DEFECTED, contacts);
					}
				}
			} else {
				if (contact.getSecondPlayerChoice() == Contact.CHOICE_COOPERATE) {
					contact.setFirstDefectCount(contact.getFirstDefectCount() + 1);
					ContactUtils.save(contact);
					if (viewer == 0) {
						return new Tupple<Integer, List<Contact>>(PLAY_RESULT_I_DEFECTED, contacts);
					} else {
						return new Tupple<Integer, List<Contact>>(PLAY_RESULT_HE_DEFECTED, contacts);
					}
				}else {
					contact.setBothDefectCount(contact.getBothDefectCount() + 1);
					ContactUtils.save(contact);
					return new Tupple<Integer, List<Contact>>(PLAY_RESULT_BOTH_DEFECTED, contacts);
				}
			}
		}
	}

	@Override
	public void askForRecommandation(String otherPlayerId) throws NotLoggedInException {
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser();
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		String playerId = blackMarketUser.getUserKey();
		System.out.println("Ask for recommandation");
		System.out.println("Asker:     " + playerId);
		System.out.println("Asked:     " + otherPlayerId);
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
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser();
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		String playerId = blackMarketUser.getUserKey();
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
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser();
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		String playerId = blackMarketUser.getUserKey();
		Contact playerOtherPlayer = ContactUtils.getContactForUsers(playerId, otherPlayerId);
		Contact playerSuggestedPlayer = ContactUtils.getContactForUsers(playerId, suggestedPlayerId);
		Contact otherSuggestedPlayer = ContactUtils.getContactForUsers(otherPlayerId, suggestedPlayerId);
		if (playerOtherPlayer != null && playerSuggestedPlayer != null && otherSuggestedPlayer == null) {
			BlackMarketUser otherUser = BlackMarketUserUtils.getUserByKey(otherPlayerId);
			BlackMarketUser suggestedUser = BlackMarketUserUtils.getUserByKey(suggestedPlayerId);
			Contact newContact = ContactUtils.createContact(otherUser, suggestedUser);
			ContactUtils.save(newContact);	
			if (playerOtherPlayer.getViewer() == 0) {
				playerOtherPlayer.setSecondPlayerRequestsRecommandation(null);
			} else {
				playerOtherPlayer.setFirstPlayerRequestsRecommandation(null);
			}
			ContactUtils.save(playerOtherPlayer);
		}
	}
}
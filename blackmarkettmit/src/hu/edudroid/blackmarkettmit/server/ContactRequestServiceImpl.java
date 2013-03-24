package hu.edudroid.blackmarkettmit.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import hu.edudroid.blackmarkettmit.client.NotLoggedInException;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestService;
import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;
import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ContactRequestServiceImpl  extends RemoteServiceServlet implements ContactRequestService{

	private static final long serialVersionUID = 2355080806520903322L;

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
			System.out.println("Checking " + possibleContact.getUserKey());
			System.out.println("For " + contacts.size() + " contacts.");
			for (Contact contact : contacts) {
				System.out.println("Matching against " + contact.getFirstPlayerKey());
				System.out.println("and " + contact.getSecondPlayerKey());
				if (contact.getFirstPlayerKey().equals(possibleContact.getUserKey())
						|| contact.getSecondPlayerKey().equals(possibleContact.getUserKey())) {
					System.out.println("Match, next try.");
					possibleContact = null;
					continue tries;
				}
			}
			// We have an eligible contact
			System.out.println("Contact found " + possibleContact.getUserKey());
			break tries;
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
		if (blackMarketUser != null) {
			return ContactUtils.getContactsForUser(blackMarketUser.getUserKey());
		} else {
			return null;
		}
	}

	@Override
	public Integer play(String otherPlayerId, int choice) throws NotLoggedInException {
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser();
		if (blackMarketUser == null) {
			throw new NotLoggedInException();
		}
		String playerId = blackMarketUser.getUserKey();
		System.out.println("Play");
		System.out.println("First:     " + playerId);
		System.out.println("Second:    " + otherPlayerId);
		System.out.println("Choice:    " + choice);
		Contact contact = ContactUtils.getContactForUsers(playerId, otherPlayerId);
		if (contact == null) {
			System.out.println("No player.");
			return PLAY_RESULT_DECLINED;
		}
		System.out.println("Viewer:    " + contact.getViewer());
		if ((choice != Contact.CHOICE_COOPERATE) && (choice != Contact.CHOICE_DEFECT) && (choice != Contact.CHOICE_REJECT)) {
			return PLAY_RESULT_DECLINED;
		}
		if (choice == Contact.CHOICE_REJECT) {
			contact.setInGame(0);
			contact.setWhoStarted(-1);
			ContactUtils.save(contact);
			return PLAY_RESULT_ACCEPTED;
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
			return 0;
		} else {
			if (contact.getWhoStarted() == contact.getViewer()) {
				return PLAY_RESULT_DECLINED;
			}
			// Old game
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
					return PLAY_RESULT_COOPERATE;
				}else {
					contact.setSecondDefectCount(contact.getSecondDefectCount() + 1);
					ContactUtils.save(contact);
					if (viewer == 0) {
						return PLAY_RESULT_HE_DEFECTED;
					} else {
						return PLAY_RESULT_I_DEFECTED;
					}
				}
			} else {
				if (contact.getSecondPlayerChoice() == Contact.CHOICE_COOPERATE) {
					contact.setFirstDefectCount(contact.getFirstDefectCount() + 1);
					ContactUtils.save(contact);
					if (viewer == 0) {
						return PLAY_RESULT_I_DEFECTED;
					} else {
						return PLAY_RESULT_HE_DEFECTED;
					}
				}else {
					contact.setBothDefectCount(contact.getBothDefectCount() + 1);
					ContactUtils.save(contact);
					return PLAY_RESULT_BOTH_DEFECTED;
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
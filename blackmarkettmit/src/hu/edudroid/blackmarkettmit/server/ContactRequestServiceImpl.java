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
	public Integer play(String playerId, String otherPlayerId, int choice) throws NotLoggedInException {
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
}
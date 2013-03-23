package hu.edudroid.blackmarkettmit.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import hu.edudroid.blackmarkettmit.client.NotLoggedInException;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestService;
import hu.edudroid.blackmarkettmit.server.persistence.BlackMarketUser;
import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.PlayerState;
import hu.edudroid.blackmarkettmit.shared.RecommandationRequest;

import com.google.appengine.api.datastore.KeyFactory;
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
		PersistenceManager userManager = PMF.get().getPersistenceManager();
		BlackMarketUser blackMarketUser = UserManager.getCurrentUser(userManager);
		List<hu.edudroid.blackmarkettmit.server.persistence.Contact> contacts = blackMarketUser.getContacts();
		try {
			contacts.get(0);
		} catch (NullPointerException e) {
			contacts = new ArrayList<hu.edudroid.blackmarkettmit.server.persistence.Contact>();
		} catch (ArrayIndexOutOfBoundsException e) {
			contacts = new ArrayList<hu.edudroid.blackmarkettmit.server.persistence.Contact>();
		}
		BlackMarketUser newContact = null;
		PersistenceManager contactManager = PMF.get().getPersistenceManager();
		tries:for (int i = 0; i < 10; i++) {
			Query q = contactManager.newQuery(BlackMarketUser.class);
			q.setFilter("userKey != :p1");
			q.setFilter("random < :p2");
			q.setOrdering("random desc");
			HashMap<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("p1", blackMarketUser.getUserKey());
			parameterMap.put("p2", (float)Math.random());
			@SuppressWarnings("unchecked")
			List<BlackMarketUser> result = (List<BlackMarketUser>) q.executeWithMap(parameterMap);
			for (BlackMarketUser possibleContact : result) {
				if (contacts.contains(possibleContact)) {
					continue tries;
				} else {
					newContact = possibleContact;
					break tries;
				}
			}
		}
		if (newContact == null) {
			userManager.close();
			return null;
		} else {
			hu.edudroid.blackmarkettmit.server.persistence.Contact dbContact = new hu.edudroid.blackmarkettmit.server.persistence.Contact();
			dbContact.setPlayer(newContact);
			dbContact.setDisplayName("Display name " + ((int)(Math.random() * 1000000)));
			contactManager.makePersistent(dbContact);
			contactManager.close();
			// Add to contact list
			contacts.add(dbContact);
			blackMarketUser.setContacts(contacts);
			userManager.close();
			Contact ret = new Contact();
			ret.setPlayerKey(KeyFactory.keyToString(newContact.getUserKey()));
			ret.setDisplayName(dbContact.getDisplayName());
			ret.setState(PlayerState.NEW);
			ArrayList<Contact> rets = new ArrayList<Contact>();
			rets.add(ret);
			return rets;
		}
	}

	@Override
	public List<Contact> getContacts() throws NotLoggedInException {
		PersistenceManager userManager = PMF.get().getPersistenceManager();
		BlackMarketUser blacMarketUser = UserManager.getCurrentUser(userManager);
		List<hu.edudroid.blackmarkettmit.server.persistence.Contact> dbContacts = blacMarketUser.getContacts();
		userManager.close();
		if (dbContacts == null) {
			return null;
		}
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		try {
			for (hu.edudroid.blackmarkettmit.server.persistence.Contact dbContact : dbContacts) {
				Contact contact = new Contact();
				contact.setPlayerKey(KeyFactory.keyToString(dbContact.getPlayer().getUserKey()));
				contact.setBothScrevedCount(dbContact.getBothScrevedCount());
				contact.setCooperationCount(dbContact.getCooperationCount());
				contact.setDisplayName(dbContact.getDisplayName());
				contact.setGameCount(dbContact.getGameCount());
				contact.setScrewedHimCount(dbContact.getScrewedHimCount());
				contact.setScrewedMeCount(dbContact.getScrewedMeCount());
				contact.setState(PlayerState.NEW);
				contacts.add(contact);
			}
			return contacts;
		} catch (NullPointerException e) {
			return null;
		}
	}
}
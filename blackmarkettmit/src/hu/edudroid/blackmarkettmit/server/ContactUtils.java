package hu.edudroid.blackmarkettmit.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;

import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;
import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.PlayerState;

public class ContactUtils {
	
	public static List<Contact> getContactsForUser(String blackMarketUserKey) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter firstPlayerFilter = new FilterPredicate("firstPlayerKey", FilterOperator.EQUAL, blackMarketUserKey);
		Filter secondPlayerFilter = new FilterPredicate("secondPlayerKey", FilterOperator.EQUAL, blackMarketUserKey);
		Filter playerFilter = CompositeFilterOperator.or(firstPlayerFilter, secondPlayerFilter);		

		// Use class Query to assemble a query
		Query q = new Query("Contact").setFilter(playerFilter);

		// Use PreparedQuery interface to retrieve results
		PreparedQuery pq = datastore.prepare(q);
		List<Contact> contacts = new ArrayList<Contact>();
		System.out.println("Requested contacts for " + blackMarketUserKey);
		for (Entity result : pq.asIterable()) {
			Contact contact = createFromEntity(result, blackMarketUserKey);
			contacts.add(contact);
		}
		return contacts;
	}

	public static Contact getContactForUsers(String playerId, String otherPlayerId) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter firstOrderFirstFilter = new FilterPredicate("firstPlayerKey", FilterOperator.EQUAL, playerId);
		Filter firstOrderSecondFilter = new FilterPredicate("secondPlayerKey", FilterOperator.EQUAL, otherPlayerId);
		Filter firstOrderFilter = CompositeFilterOperator.and(firstOrderFirstFilter, firstOrderSecondFilter);
		Filter secondOrderFirstFilter = new FilterPredicate("firstPlayerKey", FilterOperator.EQUAL, otherPlayerId);
		Filter secondOrderSecondFilter = new FilterPredicate("secondPlayerKey", FilterOperator.EQUAL, playerId);
		Filter secondOrderFilter = CompositeFilterOperator.and(secondOrderFirstFilter, secondOrderSecondFilter);
		Filter playerFilter = CompositeFilterOperator.or(firstOrderFilter, secondOrderFilter);		

		// Use class Query to assemble a query
		Query q = new Query("Contact").setFilter(playerFilter);

		// Use PreparedQuery interface to retrieve results
		PreparedQuery pq = datastore.prepare(q);
		List<Contact> contacts = new ArrayList<Contact>();
		System.out.println("Requested contacts for " + playerId + " " + otherPlayerId);
		for (Entity result : pq.asIterable()) {
			Contact contact = createFromEntity(result, playerId);
			contacts.add(contact);
		}
		if (contacts.isEmpty()) {
			return null;
		} else {
			return contacts.get(0);
		}
	}
	
	public static void save(Contact contact) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
		try {
			entity = datastore.get(KeyFactory.stringToKey(contact.getEntityKey()));
			System.out.println("Existing contact.");
		} catch (EntityNotFoundException e) {
			System.out.println("New contact.");
		} catch (NullPointerException e) {
			System.out.println("New contact.");
		} 
		if (entity == null) {
			entity = new Entity("Contact");
		}
		entity.setProperty("inGame", contact.getInGame());
		entity.setProperty("whoStarted", contact.getWhoStarted());
		entity.setProperty("firstPlayerChoice", contact.getFirstPlayerChoice());
		entity.setProperty("secondPlayerChoice", contact.getSecondPlayerChoice());
		entity.setProperty("gameStart", contact.getGameStart());

		entity.setProperty("firstPlayerKey", contact.getFirstPlayerKey());
		entity.setProperty("secondPlayerKey", contact.getSecondPlayerKey());
		entity.setProperty("firstDisplayName", contact.getFirstDisplayName());
		entity.setProperty("secondDisplayName", contact.getSecondDisplayName());
		entity.setProperty("firstDebugDisplayName", contact.getFirstDebugDisplayName());
		entity.setProperty("secondDebugDisplayName", contact.getSecondDebugDisplayName());

		entity.setProperty("gameCount", contact.getGameCount());
		entity.setProperty("cooperationCount", contact.getCooperationCount());
		entity.setProperty("bothDefectCount", contact.getBothDefectCount());
		entity.setProperty("firstDefectCount", contact.getFirstDefectCount());
		entity.setProperty("secondDefectCount", contact.getSecondDefectCount());
		datastore.put(entity);		
	}

	public static Contact createFromEntity(Entity result, String viewerKey) {
		Contact contact = new Contact();
		contact.setEntityKey(KeyFactory.keyToString(result.getKey()));
		try {
			contact.setInGame((int)((Long)result.getProperty("inGame")).longValue());
		} catch (Exception e) {
			contact.setInGame(0);
		}
		try {
			contact.setWhoStarted((int)((Long)result.getProperty("whoStarted")).longValue());
		} catch (Exception e) {
			contact.setWhoStarted(-1);
		}
		try {
			contact.setFirstPlayerChoice((int)((Long)result.getProperty("firstPlayerChoice")).longValue());
		} catch (Exception e) {
			contact.setFirstPlayerChoice(Contact.CHOICE_NONE);
		}
		try {
			contact.setSecondPlayerChoice((int)((Long)result.getProperty("secondPlayerChoice")).longValue());
		} catch (Exception e) {
			contact.setSecondPlayerChoice(Contact.CHOICE_NONE);
		}
		try {
			contact.setGameStart((Date)result.getProperty("gameStart"));
		} catch (Exception e) {
			contact.setGameStart(null);
		}

		contact.setFirstPlayerKey((String)result.getProperty("firstPlayerKey"));
		contact.setSecondPlayerKey((String)result.getProperty("secondPlayerKey"));
		contact.setFirstDisplayName((String)result.getProperty("firstDisplayName"));
		contact.setSecondDisplayName((String)result.getProperty("secondDisplayName"));
		contact.setFirstDebugDisplayName((String)result.getProperty("firstDebugDisplayName"));
		contact.setSecondDebugDisplayName((String)result.getProperty("secondDebugDisplayName"));
		
		contact.setGameCount((int)((Long)result.getProperty("gameCount")).longValue());
		contact.setCooperationCount((int)((Long)result.getProperty("cooperationCount")).longValue());
		contact.setBothDefectCount((int)((Long)result.getProperty("bothDefectCount")).longValue());
		contact.setFirstDefectCount((int)((Long)result.getProperty("firstDefectCount")).longValue());
		contact.setSecondDefectCount((int)((Long)result.getProperty("secondDefectCount")).longValue());
		
		contact.setState(PlayerState.NEW);
		
		if (contact.getGameCount() > 0) {
			contact.setState(PlayerState.NEUTRAL);
		}
		if (viewerKey.equals(contact.getFirstPlayerKey())) {
			contact.setViewer(0);
			if (contact.getInGame() == 1) {
				if (contact.getWhoStarted() == 0) {
					contact.setState(PlayerState.INVITED_HIM);
				} else {
					contact.setState(PlayerState.INVITED_ME);
				}
			}
		} else {
			contact.setViewer(1);
			if (contact.getInGame() == 1) {
				if (contact.getWhoStarted() == 0) {
					contact.setState(PlayerState.INVITED_ME);
				} else {
					contact.setState(PlayerState.INVITED_HIM);
				}
			}
		}
		return contact;
	}

	public static Contact createContact(BlackMarketUser user, BlackMarketUser contactUser) {
		Contact contact = new Contact();
		contact.setViewer(0);
		contact.setFirstPlayerKey(user.getUserKey());
		contact.setSecondPlayerKey(contactUser.getUserKey());
		contact.setFirstDisplayName("Name " + ((int)(Math.random() * 1000000)));
		contact.setSecondDisplayName("Name " + ((int)(Math.random() * 1000000)));
		contact.setFirstDebugDisplayName(user.getExternalId());
		contact.setSecondDebugDisplayName(contactUser.getExternalId());
		return contact;
	}
}

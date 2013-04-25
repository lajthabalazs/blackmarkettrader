package hu.edudroid.blackmarkettmit.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Blob;
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
		// Add history to entity
		entity.setProperty("tradeHistory", contact.getTradeHistory());

		entity.setProperty("firstPlayerKey", contact.getFirstPlayerKey());
		entity.setProperty("secondPlayerKey", contact.getSecondPlayerKey());
		entity.setProperty("firstDisplayName", contact.getFirstDisplayName());
		entity.setProperty("secondDisplayName", contact.getSecondDisplayName());
		entity.setProperty("firstDebugDisplayName", contact.getFirstDebugDisplayName());
		entity.setProperty("secondDebugDisplayName", contact.getSecondDebugDisplayName());

		entity.setProperty("firstPlayerRequestsRecommandation",contact.getFirstPlayerRequestsRecommandation());
		entity.setProperty("secondPlayerRequestsRecommandation",contact.getSecondPlayerRequestsRecommandation());

		datastore.put(entity);		
	}

	public static Contact createFromEntity(Entity result, String viewerKey) {
		Contact contact = new Contact();
		contact.setEntityKey(KeyFactory.keyToString(result.getKey()));

		contact.setFirstPlayerKey((String)result.getProperty("firstPlayerKey"));
		contact.setSecondPlayerKey((String)result.getProperty("secondPlayerKey"));
		contact.setFirstDisplayName((String)result.getProperty("firstDisplayName"));
		contact.setSecondDisplayName((String)result.getProperty("secondDisplayName"));
		contact.setFirstDebugDisplayName((String)result.getProperty("firstDebugDisplayName"));
		contact.setSecondDebugDisplayName((String)result.getProperty("secondDebugDisplayName"));
		
		if (result.hasProperty("firstPlayerRequestsRecommandation")) {
			contact.setFirstPlayerRequestsRecommandation((Date)result.getProperty("firstPlayerRequestsRecommandation"));
		} else {
			contact.setFirstPlayerRequestsRecommandation(null);
		}
		if (result.hasProperty("secondPlayerRequestsRecommandation")) {
			contact.setSecondPlayerRequestsRecommandation((Date)result.getProperty("secondPlayerRequestsRecommandation"));
		} else {
			contact.setSecondPlayerRequestsRecommandation(null);
		}
		try{
			contact.setTradeHistory((Blob) result.getProperty("tradeHistory"));
		} catch(Exception e) {
			contact.setTradeHistory(new Blob(new byte[0]));
		}

		if (viewerKey.equals(contact.getFirstPlayerKey())) {
			contact.setViewer(0);
		} else {
			contact.setViewer(1);
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

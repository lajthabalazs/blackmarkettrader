package hu.edudroid.blackmarkettmit.server;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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
		Query q = new Query("BlackMarketUser").setFilter(playerFilter);

		// Use PreparedQuery interface to retrieve results
		PreparedQuery pq = datastore.prepare(q);
		List<Contact> contacts = new ArrayList<Contact>();
		for (Entity result : pq.asIterable()) {
			Contact contact = createFromEntity(result);
			if (blackMarketUserKey.equals(contact.getFirstPlayerKey())) {
				contact.setViewer(0);
			} else {
				contact.setViewer(1);
			}
			contacts.add(contact);
		}
		return contacts;

		
	}
	
	public static void save(Contact contact) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("Contact");
		entity.setProperty("firstPlayerKey", contact.getFirstPlayerKey());
		entity.setProperty("secondPlayerKey", contact.getSecondPlayerKey());
		entity.setProperty("firstDisplayName", contact.getFirstDisplayName());
		entity.setProperty("secondDisplayName", contact.getSecondDisplayName());

		entity.setProperty("gameCount", contact.getGameCount());
		entity.setProperty("cooperationCount", contact.getCooperationCount());
		entity.setProperty("bothDefectCount", contact.getBothDefectCount());
		entity.setProperty("firstDefectCount", contact.getFirstDefectCount());
		entity.setProperty("secondDefectCount", contact.getSecondDefectCount());
		datastore.put(entity);		
	}

	public static Contact createFromEntity(Entity result) {
		Contact contact = new Contact();
		contact.setFirstPlayerKey((String)result.getProperty("firstPlayerKey"));
		contact.setSecondPlayerKey((String)result.getProperty("secondPlayerKey"));
		contact.setFirstDisplayName((String)result.getProperty("firstDisplayName"));
		contact.setSecondDisplayName((String)result.getProperty("secondDisplayName"));
		
		contact.setGameCount((Integer)result.getProperty("gameCount"));
		contact.setCooperationCount((Integer)result.getProperty("cooperationCount"));
		contact.setBothDefectCount((Integer)result.getProperty("bothDefectCount"));
		contact.setFirstDefectCount((Integer)result.getProperty("firstDefectCount"));
		contact.setSecondDefectCount((Integer)result.getProperty("secondDefectCount"));
		return contact;
	}

	public static Contact createContact(BlackMarketUser user, BlackMarketUser contactUser) {
		Contact contact = new Contact();
		contact.setViewer(0);
		contact.setFirstPlayerKey(user.getUserKey());
		contact.setSecondPlayerKey(contactUser.getUserKey());
		contact.setFirstDisplayName("Name " + ((int)(Math.random() * 1000000)));
		contact.setSecondDisplayName("Name " + ((int)(Math.random() * 1000000)));
		return contact;
	}
}

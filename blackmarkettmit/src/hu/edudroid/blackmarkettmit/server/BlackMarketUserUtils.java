package hu.edudroid.blackmarkettmit.server;

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
import com.google.appengine.api.datastore.Query.SortDirection;

import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;

public class BlackMarketUserUtils {
	
	public static BlackMarketUser getUserByExternalId(String externalId) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Filter externalKeyFilter = new FilterPredicate("externalId",
				FilterOperator.EQUAL, externalId);

		// Use class Query to assemble a query
		Query q = new Query("BlackMarketUser").setFilter(externalKeyFilter);
		PreparedQuery pq = datastore.prepare(q);

		for (Entity result : pq.asIterable()) {
			System.out.println("Whe have a registered user");
			return BlackMarketUserUtils.createFromEntity(result);
		}
		System.out.println("No user found.");
		return null;
	}
	
	public static BlackMarketUser getUserByKey(String userKey) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity entity = datastore.get(KeyFactory.stringToKey(userKey));
			return createFromEntity(entity);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static BlackMarketUser getRandomUser() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		double random = Math.random();
		Filter randomFilter = new FilterPredicate("random", FilterOperator.LESS_THAN_OR_EQUAL, random);
		System.out.println("Random value " + random);
		Query q = new Query("BlackMarketUser").setFilter(randomFilter).addSort("random",SortDirection.DESCENDING);
		PreparedQuery pq = datastore.prepare(q);

		for (Entity result : pq.asIterable()) {
			return BlackMarketUserUtils.createFromEntity(result);
		}
		return null;
	}


	public static void save(BlackMarketUser blackMarketUser) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("BlackMarketUser");
		entity.setProperty("externalId", blackMarketUser.getExternalId());
		entity.setProperty("random", blackMarketUser.getRandom());
		datastore.put(entity);		
	}

	public static BlackMarketUser createFromEntity(Entity result) {
		BlackMarketUser user = new BlackMarketUser();
		user.setExternalId((String)result.getProperty("externalId"));
		user.setRandom(((Double)result.getProperty("random")).floatValue());
		user.setUserKey(KeyFactory.keyToString(result.getKey()));
		return user;
	}
}

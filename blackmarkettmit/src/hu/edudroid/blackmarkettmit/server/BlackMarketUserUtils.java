package hu.edudroid.blackmarkettmit.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;

public class BlackMarketUserUtils {
	
	public static BlackMarketUser getUserByEmail(String emailAddress) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Filter externalKeyFilter = new FilterPredicate("externalId",
				FilterOperator.EQUAL, emailAddress);

		// Use class Query to assemble a query
		Query q = new Query("BlackMarketUser").setFilter(externalKeyFilter);
		PreparedQuery pq = datastore.prepare(q);

		for (Entity result : pq.asIterable()) {
			return BlackMarketUserUtils.createFromEntity(result);
		}
		return null;
	}
	
	public static BlackMarketUser getRandomUser() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter randomFilter = new FilterPredicate("random", FilterOperator.LESS_THAN_OR_EQUAL, Math.random());
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

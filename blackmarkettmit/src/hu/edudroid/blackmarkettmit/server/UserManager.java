package hu.edudroid.blackmarkettmit.server;

import hu.edudroid.blackmarkettmit.server.persistence.BlackMarketUserUtils;
import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class UserManager {
	public static BlackMarketUser getCurrentUser() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		return getCurrentUser(user);
	}

	public static BlackMarketUser getCurrentUser(User user) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Filter externalKeyFilter = new FilterPredicate("externalId",
				FilterOperator.EQUAL, user.getEmail());

		// Use class Query to assemble a query
		Query q = new Query("BlackMarketUser").setFilter(externalKeyFilter);

		// Use PreparedQuery interface to retrieve results
		PreparedQuery pq = datastore.prepare(q);

		for (Entity result : pq.asIterable()) {
			return BlackMarketUserUtils.createFromEntity(result);
		}
		return null;
	}
}

package hu.edudroid.blackmarkettmit.server.persistence;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;

public class BlackMarketUserUtils {
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
		user.setUserKey(result.getKey().toString());
		return user;
	}
}

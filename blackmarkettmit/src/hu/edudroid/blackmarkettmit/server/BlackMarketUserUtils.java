package hu.edudroid.blackmarkettmit.server;

import java.util.Calendar;

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
import com.google.appengine.api.datastore.Query.SortDirection;

import hu.edudroid.blackmarkettmit.shared.BlackMarketUser;
import hu.edudroid.blackmarkettmit.shared.Contact;

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
		System.out.println("Random value used to find a user" + random);
		Query q = new Query("BlackMarketUser").setFilter(randomFilter).addSort("random",SortDirection.DESCENDING);
		PreparedQuery pq = datastore.prepare(q);

		for (Entity result : pq.asIterable()) {
			System.out.println("Found a user");
			return BlackMarketUserUtils.createFromEntity(result);
		}
		System.out.println("No user found.");
		return null;
	}

	private static void addLoginEvent(BlackMarketUser blackMarketUser, boolean realLogin) {
		System.out.println("Adding login entry");
		byte[] dates = blackMarketUser.getLoginDates();
		Calendar calendar = Calendar.getInstance();
		int nextIndex = dates.length;
		byte[] newDates = new byte[nextIndex + BlackMarketUser.LOGIN_HISTORY_ENTRY_LENGTH];
		System.arraycopy(dates, 0, newDates, 0, nextIndex);
		newDates[nextIndex] = (byte)(calendar.get(Calendar.YEAR) - Contact.START_YEAR);
		nextIndex++;
		newDates[nextIndex] = (byte)(calendar.get(Calendar.MONTH));
		nextIndex++;
		newDates[nextIndex] = (byte)(calendar.get(Calendar.DAY_OF_MONTH) - 1);
		nextIndex++;
		newDates[nextIndex] = (byte)(calendar.get(Calendar.HOUR));
		nextIndex++;
		newDates[nextIndex] = (byte)(calendar.get(Calendar.MINUTE));
		nextIndex++;
		newDates[nextIndex] = (byte)(calendar.get(Calendar.SECOND));
		nextIndex++;
		newDates[nextIndex] = (realLogin?(byte)1:(byte)0);
		blackMarketUser.setLoginDates(newDates);
	}

	public static void addLoginEvent(BlackMarketUser blackMarketUser) {
		System.out.println("Real login");
		addLoginEvent(blackMarketUser, true);
	}

	public static void userRequestOccured(BlackMarketUser blackMarketUser) {
		System.out.println("Checking login entry");
		byte[] dates = blackMarketUser.getLoginDates();
		Calendar currentTime = Calendar.getInstance();
		if (dates.length == 0) {
			System.out.println("First login ever");
			addLoginEvent(blackMarketUser, false);
		} else if ((dates[dates.length - BlackMarketUser.LOGIN_HISTORY_ENTRY_LENGTH] != (byte)(currentTime.get(Calendar.YEAR) - Contact.START_YEAR)) ||
				(dates[dates.length - BlackMarketUser.LOGIN_HISTORY_ENTRY_LENGTH + 1] != (byte)(currentTime.get(Calendar.MONTH))) ||
				(dates[dates.length - BlackMarketUser.LOGIN_HISTORY_ENTRY_LENGTH + 2] != (byte)(currentTime.get(Calendar.DAY_OF_MONTH) - 1))) {
			System.out.println("No login today");
			addLoginEvent(blackMarketUser, false);
		}
		// Notifications and achievements have been displayed for sure
		// When these will be on different tabs, add functions to store state
		blackMarketUser.setLastNotificationView(currentTime.getTimeInMillis());
		blackMarketUser.setLastRewardView(currentTime.getTimeInMillis());
		BlackMarketUserUtils.save(blackMarketUser);
	}

	public static void save(BlackMarketUser blackMarketUser) {
		System.out.println("Saving user");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
		try {
			entity = datastore.get(KeyFactory.stringToKey(blackMarketUser.getEntityKey()));
			System.out.println("Existing user.");
		} catch (EntityNotFoundException e) {
			System.out.println("New user.");
		} catch (NullPointerException e) {
			System.out.println("New user.");
		}
		if (entity == null) {
			entity = new Entity("BlackMarketUser");
		}
		entity.setProperty("externalId", blackMarketUser.getExternalId());
		entity.setProperty("random", blackMarketUser.getRandom());
		entity.setProperty("emailAddress", blackMarketUser.getEmailAddress());
		entity.setProperty("gender", blackMarketUser.getGender());
		entity.setProperty("birthday", blackMarketUser.getBirthday());
		entity.setProperty("lastNotification", blackMarketUser.getLastNotificationView());
		entity.setProperty("lastReward", blackMarketUser.getLastRewardView());
		entity.setProperty("loginDates", new Blob(blackMarketUser.getLoginDates()));
		System.out.println("Saved login dates " + (blackMarketUser.getLoginDates().length/BlackMarketUser.LOGIN_HISTORY_ENTRY_LENGTH));
		datastore.put(entity);
	}

	public static BlackMarketUser createFromEntity(Entity result) {
		BlackMarketUser user = new BlackMarketUser();
		user.setExternalId((String)result.getProperty("externalId"));
		user.setRandom(((Double)result.getProperty("random")).floatValue());
		try {
			user.setEmailAddress((String)result.getProperty("emailAddress"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			user.setGender((String)result.getProperty("gender"));
		} catch (Exception e) {
			e.printStackTrace();			
		}
		try {
			user.setLastNotificationView(Long.parseLong((String)result.getProperty("lastNotification")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			user.setLastRewardView(Long.parseLong((String)result.getProperty("lastReward")));
		} catch (Exception e) {
			e.printStackTrace();			
		}
		try {
			user.setBirthday((String)result.getProperty("birthday"));
		} catch (Exception e) {
			e.printStackTrace();			
		}
		user.setEntityKey(KeyFactory.keyToString(result.getKey()));
		try {
			user.setLoginDates(((Blob)result.getProperty("loginDates")).getBytes());
			System.out.println("Login dates retrieved " + user.getLoginDates().length);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return user;
	}
}

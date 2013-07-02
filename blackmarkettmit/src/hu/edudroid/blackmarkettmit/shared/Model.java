package hu.edudroid.blackmarkettmit.shared;

import hu.edudroid.blackmarkettmit.client.GWTDayCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Model {
	private List<Contact> contactList;
	private LoginInfo loginInfo;
	private int totalScore = 0;
	private int remainingEnergy = 0;
	private LoginBasedRewardsAndBadges rewards;
	private DayCaclulator dayCalculator;
	private SortedSet<Notification> notifications = new TreeSet<Notification>();
	
	public Model(DayCaclulator dayCalculator) {
		this.dayCalculator = dayCalculator;
	}
	
	public void setLoginInfo(LoginInfo loginInfo) {
		this.loginInfo = loginInfo;
		if (loginInfo == null){
			return;
		}
		rewards = new LoginBasedRewardsAndBadges(loginInfo.getBlackMarketUser().getLoginDates(), dayCalculator);		
	}

	public void setContacts(List<Contact> contacts) {
		notifications = new TreeSet<Notification>();
		totalScore = 0;
		totalScore = totalScore + rewards.getTotalBonus();
		Date currentDate = GWTDayCalculator.DAY_CALCULATOR.fromDate(new java.util.Date());
		this.contactList = contacts;
		if (loginInfo != null) {
			int usedEnergy = 0;
			for (Contact contact : contactList) {
				// Check if contact creation reduced energy
				if (contact.getViewer() == contact.getWhoRequested()) {
					Date requestDate = GWTDayCalculator.DAY_CALCULATOR.fromDate(contact.getRequestDate());
					if (requestDate.sameDay(currentDate)) {
						usedEnergy += Contact.ENERGY_CONSUMPTION_CONTACT_REQUEST;
					}
				}
				// Check if contact request reduced energy
				if (contact.getViewer() == 0) {
					if (contact.getFirstPlayerRequestsRecommandation() != null &&
							 GWTDayCalculator.DAY_CALCULATOR.fromDate(contact.getFirstPlayerRequestsRecommandation()).sameDay(currentDate)) {
						usedEnergy += Contact.ENERGY_CONSUMPTION_CONTACT_REQUEST;
					}
				} else {
					if (contact.getSecondPlayerRequestsRecommandation() != null &&
							 GWTDayCalculator.DAY_CALCULATOR.fromDate(contact.getSecondPlayerRequestsRecommandation()).sameDay(currentDate)) {
						usedEnergy += Contact.ENERGY_CONSUMPTION_CONTACT_REQUEST;
					}
				}
				List<TradingEvent> events = contact.getEvents();
				for (TradingEvent event : events) {
					totalScore += event.getPointValue();
					usedEnergy += event.getUsedEnergy(currentDate);
				}
			}
			remainingEnergy = loginInfo.getBlackMarketUser().getMaxEnergy() - usedEnergy;
		}
		if (rewards != null) {
			Collection<Notification> rewardNotifications = rewards.getNotifications();
			if (rewardNotifications != null) {
				notifications.addAll(rewards.getNotifications());
			}
		}
	}

	public ArrayList<TradingEvent> getAllTradingEvents() {
		ArrayList<TradingEvent> allEvents = new ArrayList<TradingEvent>();
		for (Contact contact : contactList) {
			allEvents.addAll(contact.getEvents());
		}
		return allEvents;
	}

	public List<Contact> getOrderedContacts() {
		if (contactList == null) {
			contactList = new ArrayList<Contact>();
		}
		java.util.Collections.sort(contactList, new ContactComparator());
		return contactList;
	}

	public int getCurrentStreak() {
		if (rewards == null) {
			return 0;
		} else {
			return rewards.getCurrentStreak();
		}
	}

	public int getRemainingEnergy() {
		return remainingEnergy;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public List<Notification> getNotifications() {
		System.out.println("Checking notifications for user " + loginInfo.getBlackMarketUser().getLastNotificationView());
		ArrayList<Notification> ret = new ArrayList<Notification>();
		for (Notification notification : notifications) {
			System.out.println("Notification time " + notification.getTime() + " ( " + (notification.getTime() - loginInfo.getBlackMarketUser().getLastNotificationView()) + ")");
			if (notification.getTime() > loginInfo.getBlackMarketUser().getLastNotificationView()) {
				ret.add(notification);
			}
		}
		return ret;
	}
}

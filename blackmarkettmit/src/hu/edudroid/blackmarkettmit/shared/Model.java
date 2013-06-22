package hu.edudroid.blackmarkettmit.shared;

import hu.edudroid.blackmarkettmit.client.GWTDayCalculator;

import java.util.ArrayList;
import java.util.List;

public class Model {
	private List<Contact> contactList;
	private LoginInfo loginInfo;
	private int totalScore = 0;
	private int remainingEnergy = 0;
	private LoginBasedRewardsAndBadges rewards;
	private DayCaclulator dayCalculator;
	
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

	public String getCurrentStreakMessage() {
		if (getCurrentStreak() > 0) {
			// Set a pop up message
			return "Wow, you're a hard worker! " + (getCurrentStreak() + 1 ) + " days in a raw, the boss is pleased. He rewarded you with $" + LoginBasedRewardsAndBadges.streakCurrentDaysValue(getCurrentStreak());
		}
		return null;
	}

	public int getRemainingEnergy() {
		return remainingEnergy;
	}

	public int getTotalScore() {
		return totalScore;
	}
}

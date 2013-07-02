package hu.edudroid.blackmarkettmit.shared;

import hu.edudroid.blackmarkettmit.shared.Notification.Component;

import java.util.ArrayList;
import java.util.Collection;

public class LoginBasedRewardsAndBadges {
	public static final int[] STREAK_REWARDS = {10, 15, 20, 25, 30, 40, 50, 75, 110, 250};
	public static final int[] STREAK_REWARD_ACCUMULATED = new int[STREAK_REWARDS.length];
	public static final int STREAK_END_BONUS_INCREMENT = 250;
	
	static
	{
		for(int i = 0; i < STREAK_REWARDS.length; i++) {
			STREAK_REWARD_ACCUMULATED[i] = 0;
			for(int j = 0; j <= i; j++) {
				STREAK_REWARD_ACCUMULATED[i] += STREAK_REWARDS[j];
			}
		}
	}
	
	private int longestStreak = 0;
	private int currentStreak = 0;
	private int missedDays = 0;
	private int totalBonus = 0;
	private long streakDate = -1;
	
	public LoginBasedRewardsAndBadges (byte[] loginHistory, DayCaclulator dayCalculator) {
		int eventCount = loginHistory.length / BlackMarketUser.LOGIN_HISTORY_ENTRY_LENGTH;
		Date lastDate = null;
		Date streakStart = null;
		Date eventDate = null;
		Date lastStreakDate = null; // Stores only important dates: first of a days events
		for (int i = 0; i < eventCount; i++) {
			eventDate = new Date(loginHistory, i * BlackMarketUser.LOGIN_HISTORY_ENTRY_LENGTH);
			if (streakStart == null) {
				streakStart = eventDate;
			}
			if (lastDate != null) {
				int dateDiff = dayCalculator.getDaysBetween(lastDate, eventDate);
				if (dateDiff == 0) {
					missedDays = 0;
				} else if (dateDiff == 1) {
					lastStreakDate = eventDate;
				} else if (dateDiff > 1) {
					int streakLength = dayCalculator.getDaysBetween(streakStart, eventDate);
					// End of a streak, calculate bonus
					totalBonus += streakValue(streakLength);
					missedDays = dateDiff;
					if (longestStreak < streakLength) {
						longestStreak = streakLength;
					}
					streakStart = eventDate;
				}
			}
			lastDate = eventDate;
		}
		if (lastStreakDate != null) {
			currentStreak = dayCalculator.getDaysBetween(streakStart, lastStreakDate);
			streakDate = dayCalculator.toDate(lastStreakDate).getTime();
			totalBonus += streakValue(currentStreak);
		}
	}
	
	public static int streakValue(int streakLength){
		if (streakLength <= 0) {
			return 0;
		}
		int finishedStreaks = streakLength / STREAK_REWARDS.length;
		int bonus = 0;
		int finishedStreaksBonus = finishedStreaks * STREAK_REWARD_ACCUMULATED[STREAK_REWARDS.length -1];
		bonus += finishedStreaksBonus;
		int finishedStreakIncrementsBonus = Math.max(0, ((finishedStreaks - 1) * finishedStreaks) / 2) * STREAK_END_BONUS_INCREMENT;
		bonus += finishedStreakIncrementsBonus;
		if (streakLength % STREAK_REWARDS.length > 0) {
			bonus += STREAK_REWARD_ACCUMULATED[streakLength % STREAK_REWARDS.length - 1];
		}
		return bonus;		
	}

	public static int streakCurrentDaysValue(int streakLength){
		if (streakLength <= 0) {
			return 0;
		}
		int finishedStreaks = streakLength / STREAK_REWARDS.length;
		if (streakLength % STREAK_REWARDS.length > 0) {
			return STREAK_REWARDS[streakLength % STREAK_REWARDS.length - 1];
		} else {
			return Math.max(0, (finishedStreaks - 1)) * STREAK_END_BONUS_INCREMENT + STREAK_REWARDS[STREAK_REWARDS.length - 1];			
		}
	}

	public int getLongestStreak() {
		return longestStreak;
	}

	public int getCurrentStreak() {
		return 3;
		// TODO uncomment! return currentStreak;
	}

	public int getMissedDays() {
		return missedDays;
	}

	public int getTotalBonus() {
		return totalBonus;
	}

	public Collection<Notification> getNotifications() {
		if (getCurrentStreak() > 0) {
			ArrayList<Notification> notifications = new ArrayList<Notification>();
			Notification notification = new Notification(Component.LOGIN_BOX, "You got a streak bonus!", "Busy as a bee, it's the " + getCurrentStreak() + "th day you're trading. You received $" + streakValue(getCurrentStreak()) + " from the boss. Keep up the good work!", streakDate);
			notifications.add(notification);
			return notifications;
		} else {
			return null;
		}
	}
}
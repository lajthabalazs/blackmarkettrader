package hu.edudroid.blackmarkettmit.shared;

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
	private String popupMessage = null;
	
	
	public LoginBasedRewardsAndBadges (byte[] loginHistory, DayCaclulator dayCalculator, boolean daysFirstEvent) {
		int eventCount = loginHistory.length / Contact.LOGIN_HISTORY_ENTRY_LENGTH;
		Date lastDate = null;
		Date streakStart = null;
		Date eventDate = null;
		for (int i = 0; i < eventCount; i++) {
			eventDate = new Date(loginHistory, i * Contact.LOGIN_HISTORY_ENTRY_LENGTH);
			if (streakStart == null) {
				streakStart = eventDate;
			}
			if (lastDate != null) {
				int dateDiff = dayCalculator.getDaysBetween(lastDate, eventDate);
				if (dateDiff == 0) {
					daysFirstEvent = false;
					missedDays = 0;
				} else if (dateDiff == 1) {
					daysFirstEvent = true;
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
		if (eventDate != null) {
			currentStreak = dayCalculator.getDaysBetween(streakStart, eventDate);
			totalBonus += streakValue(currentStreak);
			if (daysFirstEvent) {
				// Set a pop up message
				popupMessage = "Wow, you're a hard worker! " + (currentStreak + 1 ) + " days in a raw, the boss is pleased. He rewarded you with $" + streakCurrentDaysValue(currentStreak);
			}
		}
	}
	
	private int streakValue(int streakLength){
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

	private int streakCurrentDaysValue(int streakLength){
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
		return currentStreak;
	}

	public int getMissedDays() {
		return missedDays;
	}

	public int getTotalBonus() {
		return totalBonus;
	}

	public String getPopupMessage() {
		return popupMessage;
	}

	public String getPopupTitle() {
		if (getPopupMessage() != null) {
			return "Congratulations";
		} else {
			return null;
		}
	}
}
















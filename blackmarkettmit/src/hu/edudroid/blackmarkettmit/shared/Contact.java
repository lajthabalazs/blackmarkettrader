package hu.edudroid.blackmarkettmit.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Contact implements Serializable {

	private static final long serialVersionUID = 3488387526075082543L;
	public static final int CHOICE_COOPERATE = 0;
	public static final int CHOICE_DEFECT = 1;
	public static final int CHOICE_REJECT = -1;
	public static final int CHOICE_NONE = -2;
	public static final byte HISTORY_REJECT = 0;
	public static final byte HISTORY_INVITE_AND_COOP = 1;
	public static final byte HISTORY_INVITE_AND_DEFECT = 2;
	public static final byte HISTORY_ACCEPT_AND_COOP = 3;
	public static final byte HISTORY_ACCEPT_AND_DEFECT = 4;
	public static final byte HISTORY_INVALID = -1;

	public static final int TRADE_HISTORY_ENTRY_LENGTH = 8;

	public static final int START_YEAR = 2012;
	public static final int ENERGY_CONSUMPTION_CONTACT_REQUEST = 30;
	public static final int ENERGY_CONSUMPTION_INVITE = 10;
	public static final int ENERGY_CONSUMPTION_ACCEPT = 8;
	public static final int ENERGY_CONSUMPTION_REJECT = 0;
	
	public static final int POINT_VALUE_BOTH_COOPERATE = 40;
	public static final int POINT_VALUE_BOTH_DEFECT = -10;
	public static final int POINT_VALUE_HE_DEFECTS = -40;
	public static final int POINT_VALUE_I_DEFECT = 100;

	private String entityKey;

	private int viewer;
	// Used for calculating user energy
	private int whoRequested;
	private String whoSuggested;
	private Date requestDate;
	private Date creationDate;
	// Credentials
	private String firstPlayerKey;
	private String secondPlayerKey;
	private int firstNameCode;
	private int secondNameCode;
	// Recommendation requests
	private Date firstPlayerRequestsRecommandation;
	private Date secondPlayerRequestsRecommandation;
	// Statistics
	private byte[] tradeHistory;

	public byte[] getTradeHistory() {
		if (tradeHistory == null) {
			tradeHistory = new byte[0];
		}
		return tradeHistory;
	}

	public void setTradeHistory(byte[] tradeHistory) {
		this.tradeHistory = tradeHistory;
	}

	public String getEntityKey() {
		return entityKey;
	}

	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	public int getViewer() {
		return viewer;
	}

	public void setViewer(int viewer) {
		this.viewer = viewer;
	}

	public int getWhoRequested() {
		return whoRequested;
	}

	public void setWhoRequested(int whoRequested) {
		this.whoRequested = whoRequested;
	}

	public String getWhoSuggested() {
		return whoSuggested;
	}

	public void setWhoSuggested(String whoSuggested) {
		this.whoSuggested = whoSuggested;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getFirstPlayerKey() {
		return firstPlayerKey;
	}

	public void setFirstPlayerKey(String firstPlayerKey) {
		this.firstPlayerKey = firstPlayerKey;
	}

	public String getSecondPlayerKey() {
		return secondPlayerKey;
	}

	public void setSecondPlayerKey(String secondPlayerKey) {
		this.secondPlayerKey = secondPlayerKey;
	}

	public int getFirstNameCode() {
		return firstNameCode;
	}

	public void setFirstNameCode(int firstNameCode) {
		this.firstNameCode = firstNameCode;
	}

	public int getSecondNameCode() {
		return secondNameCode;
	}

	public void setSecondNameCode(int secondNameCode) {
		this.secondNameCode = secondNameCode;
	}

	public Date getFirstPlayerRequestsRecommandation() {
		return firstPlayerRequestsRecommandation;
	}

	public void setFirstPlayerRequestsRecommandation(
			Date firstPlayerRequestsRecommandation) {
		this.firstPlayerRequestsRecommandation = firstPlayerRequestsRecommandation;
	}

	public Date getSecondPlayerRequestsRecommandation() {
		return secondPlayerRequestsRecommandation;
	}

	public void setSecondPlayerRequestsRecommandation(
			Date secondPlayerRequestsRecommandation) {
		this.secondPlayerRequestsRecommandation = secondPlayerRequestsRecommandation;
	}

	/**
	 * Examines history's last entry and determines the current state of the
	 * connection.
	 * 
	 * @return The current state of the connection.
	 */
	public PlayerState getState() {
		if (tradeHistory == null || tradeHistory.length == 0) {
			return PlayerState.NEW;
		}
		// Must have history at this point
		if ((tradeHistory[tradeHistory.length - 1] == HISTORY_INVITE_AND_COOP)
				|| (tradeHistory[tradeHistory.length - 1] == HISTORY_INVITE_AND_DEFECT)) {
			if (tradeHistory[tradeHistory.length - 2] == getViewer()) { // Actor
																		// was
																		// viewer
				return PlayerState.INVITED_HIM;
			} else {
				return PlayerState.INVITED_ME;
			}
		} else {
			return PlayerState.NEUTRAL;
		}
	}

	public int getGameCount() {
		if (tradeHistory == null) {
			return 0;
		}
		return tradeHistory.length / (TRADE_HISTORY_ENTRY_LENGTH * 2);
	}

	public boolean isLastTradeClosed() {
		if (tradeHistory == null || tradeHistory.length == 0) {
			return true;
		} else {
			int length = tradeHistory.length;
			int firstComparable = (length / (2 * TRADE_HISTORY_ENTRY_LENGTH))
					* 2 * TRADE_HISTORY_ENTRY_LENGTH;
			return firstComparable == length;
		}
	}

	public int getWhoStarted() {
		if (isLastTradeClosed()) {
			return -1;
		} else {
			return tradeHistory[tradeHistory.length - 2];
		}
	}

	public List<TradingEvent> getEvents() {
		String contactName = NameUtils.getName((viewer == 0)?secondNameCode:firstNameCode);
		ArrayList<TradingEvent> events = new ArrayList<TradingEvent>();
		int eventCount = tradeHistory.length
				/ (Contact.TRADE_HISTORY_ENTRY_LENGTH * 2);
		if (tradeHistory.length != eventCount * 2
				* Contact.TRADE_HISTORY_ENTRY_LENGTH) {
			eventCount++;
		}
		for (int i = 0; i < eventCount; i++) {
			TradingEvent event = new TradingEvent(getViewer(), this.getTradeHistory(), i, contactName);
			events.add(event);
		}
		return events;
	}

	public String getFirstDisplayName() {
		return NameUtils.getName(getFirstNameCode());
	}

	public String getSecondDisplayName() {
		return NameUtils.getName(getSecondNameCode());
	}	
}

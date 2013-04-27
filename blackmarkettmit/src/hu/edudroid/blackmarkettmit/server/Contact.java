package hu.edudroid.blackmarkettmit.server;

import hu.edudroid.blackmarkettmit.shared.PlayerState;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Blob;

public class Contact implements Serializable{

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

	public static final int TRADE_HISTORY_ENTRY_LENGTH = 8;
	public static final int START_YEAR = 2012;

	private String entityKey;
	
	private int viewer;
	// Credentials
	private String firstPlayerKey;
	private String secondPlayerKey;
	private String firstDisplayName;
	private String secondDisplayName;
	private String firstDebugDisplayName;
	private String secondDebugDisplayName;
	// Recommendation requests
	private Date firstPlayerRequestsRecommandation;
	private Date secondPlayerRequestsRecommandation;
	// Statistics
	private Blob tradeHistory;
	private Blob contactHistory;
	
	
	public Blob getTradeHistory() {
		if (tradeHistory == null) {
			tradeHistory = new Blob(new byte[0]);
		}
		return tradeHistory;
	}

	public void setTradeHistory(Blob tradeHistory) {
		this.tradeHistory = tradeHistory;
	}

	public Blob getContactHistory() {
		return contactHistory;
	}

	public void setContactHistory(Blob contactHistory) {
		this.contactHistory = contactHistory;
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

	public String getFirstDisplayName() {
		return firstDisplayName;
	}

	public void setFirstDisplayName(String firstDisplayName) {
		this.firstDisplayName = firstDisplayName;
	}

	public String getSecondDisplayName() {
		return secondDisplayName;
	}

	public void setSecondDisplayName(String secondDisplayName) {
		this.secondDisplayName = secondDisplayName;
	}

	public String getFirstDebugDisplayName() {
		return firstDebugDisplayName;
	}

	public void setFirstDebugDisplayName(String firstDebugDisplayName) {
		this.firstDebugDisplayName = firstDebugDisplayName;
	}

	public String getSecondDebugDisplayName() {
		return secondDebugDisplayName;
	}

	public void setSecondDebugDisplayName(String secondDebugDisplayName) {
		this.secondDebugDisplayName = secondDebugDisplayName;
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
	 * Examines history's last entry and determines the current state of the connection.
	 * @return The current state of the connection.
	 */
	public PlayerState getState() {
		if (tradeHistory == null || tradeHistory.getBytes().length == 0) {
			return PlayerState.NEW;
		}
		// Must have history at this point
		byte[] tradeHistory = this.tradeHistory.getBytes();
		if ((tradeHistory[tradeHistory.length - 1] == HISTORY_INVITE_AND_COOP)||(tradeHistory[tradeHistory.length - 1] == HISTORY_INVITE_AND_DEFECT)) {
			if (((tradeHistory[tradeHistory.length - 2] == 0)&&(getViewer() == 0)) ||
					((tradeHistory[tradeHistory.length - 2] == 1)&&(getViewer() == 1))) { // Actor was viewer
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
		return tradeHistory.getBytes().length / TRADE_HISTORY_ENTRY_LENGTH;
	}

	public boolean isLastTradeClosed() {
		if (tradeHistory == null || tradeHistory.getBytes().length == 0) {
			return true;
		} else {
			byte[] tradeHistory = this.tradeHistory.getBytes();
			return ((tradeHistory[tradeHistory.length - 1] == Contact.HISTORY_ACCEPT_AND_COOP) ||
					(tradeHistory[tradeHistory.length - 1] == Contact.HISTORY_ACCEPT_AND_DEFECT)||
					(tradeHistory[tradeHistory.length - 1] == Contact.HISTORY_REJECT)); 
		}
	}

	public int getWhoStarted() {
		if (isLastTradeClosed()) {
			return -1;
		} else {
			byte[] tradeHistory = this.tradeHistory.getBytes();
			return tradeHistory[tradeHistory.length - 2]; 
		}
	}
}
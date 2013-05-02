package hu.edudroid.blackmarkettmit.shared;

public class TradingEvent extends Event {
	private Contact contact;
	private int player;
	private byte event;
	private Outcome outcome;
	private byte previousEvent;
	private int initialEventEnergyConsumption;
	private Date previousEventDate;
	
	public TradingEvent(Contact contact, int index) {
		super(contact.getTradeHistory(), getStartPosition(index, contact.getTradeHistory().length));
		byte[] tradeHistory = contact.getTradeHistory();
		this.contact = contact;
		int startPosition = getStartPosition(index, contact.getTradeHistory().length);
		if (index * Contact.TRADE_HISTORY_ENTRY_LENGTH * 2 + Contact.TRADE_HISTORY_ENTRY_LENGTH * 2 <= tradeHistory.length) {
			previousEvent = tradeHistory[startPosition - 1];
			int previousPlayer = tradeHistory[startPosition - 2];
			if (previousEvent == Contact.HISTORY_INVITE_AND_COOP && previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
				if (previousPlayer == contact.getViewer()) {
					initialEventEnergyConsumption = Contact.ENERGY_CONSUMPTION_INVITE;
				}
			} else {
				initialEventEnergyConsumption = 0;
			}
			previousEventDate = new Date((tradeHistory[startPosition - Contact.TRADE_HISTORY_ENTRY_LENGTH] + Contact.START_YEAR),
					tradeHistory[startPosition - Contact.TRADE_HISTORY_ENTRY_LENGTH + 1],
					tradeHistory[startPosition - Contact.TRADE_HISTORY_ENTRY_LENGTH + 2]);		
			
		} else {
			initialEventEnergyConsumption = 0;
			previousEventDate = null;
		}
		this.player = tradeHistory[startPosition + 6];
		this.event = tradeHistory[startPosition + 7];
		// See what happened
		if (player != contact.getViewer()) { // Other player's move
			if ((event == Contact.HISTORY_INVITE_AND_COOP)||(event == Contact.HISTORY_INVITE_AND_DEFECT)) {
				outcome = Outcome.HE_INVITES;
			} else if (event == Contact.HISTORY_REJECT) {
				outcome = Outcome.HE_REJECTS;
			} else {
				if (event == Contact.HISTORY_ACCEPT_AND_COOP) {
					if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
						outcome = Outcome.I_STARTED_BOTH_COOPERATE;
					} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
						outcome = Outcome.I_STARTED_I_DEFECT;
					}
				} else if (event == Contact.HISTORY_ACCEPT_AND_DEFECT) {
					if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
						outcome = Outcome.I_STARTED_HE_DEFECTS;	
					} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
						outcome = Outcome.I_STARTED_BOTH_DEFECT;
					}
				}
			}
		} else { // My move
			if ((event == Contact.HISTORY_INVITE_AND_COOP)||(event == Contact.HISTORY_INVITE_AND_DEFECT)) {
				outcome = Outcome.I_INVITE;
			} else if (event == Contact.HISTORY_REJECT) {
				outcome = Outcome.I_REJECT;
			} else {
				if (event == Contact.HISTORY_ACCEPT_AND_COOP) {
					if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
						outcome = Outcome.HE_STARTED_BOTH_COOPERATE;
					} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
						outcome = Outcome.HE_STARTED_HE_DEFECTS;	
					}
				} else if (event == Contact.HISTORY_ACCEPT_AND_DEFECT) {
					if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
						outcome = Outcome.HE_STARTED_I_DEFECT;
					} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
						outcome = Outcome.HE_STARTED_BOTH_DEFECT;
					}
				}
			}
		}
	}
	
	private static int getStartPosition(int index, int tradeHistoryLength) {
		int startPosition = index * Contact.TRADE_HISTORY_ENTRY_LENGTH * 2;
		if (startPosition + Contact.TRADE_HISTORY_ENTRY_LENGTH * 2 <= tradeHistoryLength) {
			startPosition = startPosition + Contact.TRADE_HISTORY_ENTRY_LENGTH;
		}
		return startPosition;
	}
	
	public int getPointValue(){
		return outcome.getPoint();
	}

	public int getUsedEnergy(Date currentDate){
		int usedEnergy = 0;
		if (previousEventDate != null && previousEventDate.equals(currentDate)) {
			usedEnergy += initialEventEnergyConsumption;
		}
		if (getDate().equals(currentDate)) {
			usedEnergy += outcome.getUsedEnergy();
		} 
		return usedEnergy;
	}
	
	public Date getDate(){
		return date;
	}

	@Override
	public String toString() {
		String base = "";
		String playerName = contact.getSecondDisplayName();
		if (contact.getViewer() != 0) {
			playerName = contact.getFirstDisplayName();
		}
		return base + dateString + " - " + outcome.generateString(playerName) + "(" + outcome.getUsedEnergy() + ", " + outcome.getPoint() + ")";
	}
	
	static String byteArrayToDisplayString(byte[] array) {
		String ret = "";
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				if (i % 8 == 0) {
					ret = ret + " / ";
				} else {
					ret = ret +", ";
				}
			}
			ret = ret + array[i];
		}
		return ret;
	}
}
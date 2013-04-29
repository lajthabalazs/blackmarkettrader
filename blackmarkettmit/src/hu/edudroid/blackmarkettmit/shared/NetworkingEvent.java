package hu.edudroid.blackmarkettmit.shared;

public class NetworkingEvent extends Event{
	private Contact contact;
	private int player;
	private byte event;
	private Outcome outcome;
	private byte previousEvent;

	public static final int INVITE_ENERGY = 10;
	public static final int ACCEPT_ENERGY = 8;
	public static final int REJECT_ENERGY = 0;
	
	private enum Outcome {
		I_STARTED_BOTH_COOPERATE(INVITE_ENERGY, 40),
		I_STARTED_BOTH_DEFECT(INVITE_ENERGY, -10),
		I_STARTED_I_DEFECT(INVITE_ENERGY, 100),
		I_STARTED_HE_DEFECTS(INVITE_ENERGY, -40),
		I_REJECT(REJECT_ENERGY, 0),
		HE_REJECTS(INVITE_ENERGY, 0),
		I_INVITE(INVITE_ENERGY, 0),
		HE_INVITES(0, 0),
		HE_STARTED_BOTH_COOPERATE(ACCEPT_ENERGY, 40),
		HE_STARTED_BOTH_DEFECT(ACCEPT_ENERGY, -10),
		HE_STARTED_I_DEFECT(ACCEPT_ENERGY, 100),
		HE_STARTED_HE_DEFECTS(ACCEPT_ENERGY, -40);
		private final int point;
		private final int usedEnergy;
		private Outcome(int usedEnergy, int point) {
			this.usedEnergy = usedEnergy;
			this.point = point;
		}
		public int getPoint(){
			return point;
		}
		
		public int getUsedEnergy(){
			return usedEnergy;
		}
		public String generateString(String playerName){
			switch(this) {
			case HE_STARTED_BOTH_COOPERATE: case I_STARTED_BOTH_COOPERATE:
				return playerName + " and you cooperated.";
			case HE_STARTED_BOTH_DEFECT: case I_STARTED_BOTH_DEFECT:
				return playerName + " and you both defected.";
			case HE_STARTED_HE_DEFECTS: case I_STARTED_HE_DEFECTS:
				return playerName + " defected on you.";
			case HE_INVITES:
				return playerName + " invited you to trade.";
			case HE_REJECTS:
				return playerName + " rejected you invitation.";
			case HE_STARTED_I_DEFECT: case I_STARTED_I_DEFECT:
				return " you defected on " + playerName;
			case I_INVITE:
				return " you invited " + playerName + " to trade.";
			case I_REJECT:
				return " you rejected " + playerName + "'s invitation.";
			default:
				return " unknown event with " + playerName;
			}
		}
	}

	public NetworkingEvent(Contact contact, int index) {
		super(contact.getTradeHistory(), getStartPosition(index, contact.getTradeHistory().length));
		byte[] tradeHistory = contact.getTradeHistory();
		this.contact = contact;
		int startPosition = getStartPosition(index, contact.getTradeHistory().length);
		if (index * Contact.TRADE_HISTORY_ENTRY_LENGTH * 2 + Contact.TRADE_HISTORY_ENTRY_LENGTH * 2 <= tradeHistory.length) {
			previousEvent = tradeHistory[startPosition + 7];
		} else {
			previousEvent = Contact.HISTORY_INVALID;
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

	public int getUsedEnergy(){
		return outcome.getUsedEnergy();
	}
	
	public Date getDate(){
		return date;
	}

	@Override
	public String toString() {
		String base = ""; //byteArrayToDisplayString(contact.getTradeHistory());
		String playerName = contact.getSecondDisplayName();
		if (contact.getViewer() != 0) {
			playerName = contact.getFirstDisplayName();
		}
		return base + " " + date.toString() + " - " + outcome.generateString(playerName) + "(" + getUsedEnergy() + ", " + getPointValue() + ")";
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
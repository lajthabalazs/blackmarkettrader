package hu.edudroid.blackmarkettmit.shared;

import java.util.Comparator;

public class Event {
	Contact contact;
	int player;
	byte event;
	Outcome outcome;
	byte previousEvent;
	String dateString;

	public static final Comparator<Event> EVENT_COMPARATOR = new Comparator<Event>() {

		@Override
		public int compare(Event e1, Event e2) {
			return (-1) * e1.dateString.compareTo(e2.dateString);
		}
	};

	private enum Outcome {
		BOTH_COOPERATE(-10,10),
		BOTH_DEFECT(-10,-5),
		I_DEFECT(-10,15),
		HE_DEFECTS(-10,-10),
		I_REJECT(0,0),
		HE_REJECTS(-10,0),
		I_INVITE(-10,0),
		HE_INVITES(0,0);
		private final int point;
		private final int energy;
		private Outcome(int energy, int point) {
			this.energy = energy;
			this.point = point;
		}
		public int getPoint(){
			return point;
		}
		
		public int getEnergy(){
			return energy;
		}
		public String generateString(String playerName){
			switch(this) {
			case BOTH_COOPERATE:
				return playerName + " and you cooperated.";
			case BOTH_DEFECT:
				return playerName + " and you both defected.";
			case HE_DEFECTS:
				return playerName + " defected on you.";
			case HE_INVITES:
				return playerName + " invited you to trade.";
			case HE_REJECTS:
				return playerName + " rejected you invitation.";
			case I_DEFECT:
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

	public Event(Contact contact, int index) {
		byte[] tradeHistory = contact.getTradeHistory();
		this.contact = contact;
		int startPosition = index * Contact.TRADE_HISTORY_ENTRY_LENGTH * 2;
		if (startPosition + Contact.TRADE_HISTORY_ENTRY_LENGTH * 2 <= tradeHistory.length) {
			previousEvent = tradeHistory[startPosition + 7];
			startPosition = startPosition + Contact.TRADE_HISTORY_ENTRY_LENGTH;
		} else {
			previousEvent = Contact.HISTORY_INVALID;
		}
		dateString = "" + (tradeHistory[startPosition] + Contact.START_YEAR);
		dateString = dateString + "-" + tradeHistory[startPosition + 1];
		dateString = dateString + "-" + tradeHistory[startPosition + 2];
		dateString = dateString + " " + tradeHistory[startPosition + 3];
		dateString = dateString + ":" + tradeHistory[startPosition + 4];
		dateString = dateString + ":" + tradeHistory[startPosition + 5];
		this.player = tradeHistory[startPosition + 6];
		this.event = tradeHistory[startPosition + 7];
		// See what happened
		if (player != contact.getViewer()) {
			if ((event == Contact.HISTORY_INVITE_AND_COOP)||(event == Contact.HISTORY_INVITE_AND_DEFECT)) {
				outcome = Outcome.HE_INVITES;
			} else if (event == Contact.HISTORY_REJECT) {
				outcome = Outcome.HE_REJECTS;
			} else {
				if (event == Contact.HISTORY_ACCEPT_AND_COOP) {
					if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
						outcome = Outcome.BOTH_COOPERATE;
					} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
						outcome = Outcome.I_DEFECT;
					}
				} else if (event == Contact.HISTORY_ACCEPT_AND_DEFECT) {
					if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
						outcome = Outcome.HE_DEFECTS;	
					} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
						outcome = Outcome.BOTH_DEFECT;
					}
				}
			}
		} else {
			if ((event == Contact.HISTORY_INVITE_AND_COOP)||(event == Contact.HISTORY_INVITE_AND_DEFECT)) {
				outcome = Outcome.I_INVITE;
			} else if (event == Contact.HISTORY_REJECT) {
				outcome = Outcome.I_REJECT;
			} else {
				if (event == Contact.HISTORY_ACCEPT_AND_COOP) {
					if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
						outcome = Outcome.BOTH_COOPERATE;
					} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
						outcome = Outcome.HE_DEFECTS;	
					}
				} else if (event == Contact.HISTORY_ACCEPT_AND_DEFECT) {
					if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
						outcome = Outcome.I_DEFECT;							
					} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
						outcome = Outcome.BOTH_DEFECT;
					}
				}
			}
		}
	}
	
	public int getPointValue(){
		return outcome.getPoint();
	}

	public int getEnergy(){
		return outcome.getEnergy();
	}

	@Override
	public String toString() {
		String base =  byteArrayToDisplayString(contact.getTradeHistory());
		String playerName = contact.getSecondDisplayName();
		if (contact.getViewer() != 0) {
			playerName = contact.getFirstDisplayName();
		}
		return base + " " + dateString + " - " + outcome.generateString(playerName) + "(" + getEnergy() + ", " + getPointValue() + ")";
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
package hu.edudroid.blackmarkettmit.shared;

import java.util.Comparator;

import com.google.gwt.i18n.client.DateTimeFormat;

public class TradingEvents {
	private Contact contact;
	private int player;
	private byte event;
	private Outcome outcome;
	private byte previousEvent;
	private String dateString;
	private Date date;

	public static final Comparator<TradingEvents> EVENT_COMPARATOR = new Comparator<TradingEvents>() {

		@Override
		public int compare(TradingEvents e1, TradingEvents e2) {
			return (-1) * e1.dateString.compareTo(e2.dateString);
		}
	};

	public static final int INVITE_ENERGY = 10;
	public static final int REJECT_ENERGY = 0;
	
	private enum Outcome {
		BOTH_COOPERATE(INVITE_ENERGY,10),
		BOTH_DEFECT(INVITE_ENERGY,-5),
		I_DEFECT(INVITE_ENERGY,15),
		HE_DEFECTS(INVITE_ENERGY,-10),
		I_REJECT(REJECT_ENERGY,0),
		HE_REJECTS(INVITE_ENERGY,0),
		I_INVITE(INVITE_ENERGY,0),
		HE_INVITES(0,0);
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

	public TradingEvents(Contact contact, int index) {
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
		date = new Date((tradeHistory[startPosition] + Contact.START_YEAR), tradeHistory[startPosition + 1], tradeHistory[startPosition + 2]);
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
	
	/**
	 * A simple date class containing only year, month and day
	 * @author lajthabalazs
	 *
	 */
	public static class Date {
		public final int year;
		public final int month;
		public final int day;
		public final int hash;
		
		public Date(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
			this.hash = year * 10000 + month + day;
		}

		public Date() {
			java.util.Date now = new java.util.Date();
			year = Integer.parseInt(DateTimeFormat.getFormat( "y" ).format(now));
			month = Integer.parseInt(DateTimeFormat.getFormat( "M" ).format(now)) - 1;
			day = Integer.parseInt(DateTimeFormat.getFormat( "d" ).format(now));
			this.hash = year * 10000 + month + day;
		}

		@Override
		public String toString() {
			return year + "-" + month + "-" + day;
		}
		
		@Override
		public int hashCode() {
			return hash;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (! (obj instanceof Date)) {
				return false;
			}
			Date other = (Date) obj;
			return (this.year == other.year) && (this.month == other.month) && (this.day == other.day);
		}
	}
}
package hu.edudroid.blackmarkettmit.shared;

public class TradingEvent extends Event {
	private int firstPlayer;
	private byte firstEvent;
	private byte response;
	private Date responseDate;
	private Outcome outcome;
	private String otherPlayersName;
	
	public TradingEvent(int viewer, byte[] tradeHistory, int index, String otherPlayersName) {
		super(tradeHistory, index * Contact.TRADE_HISTORY_ENTRY_LENGTH * 2);
		int startPosition = index * Contact.TRADE_HISTORY_ENTRY_LENGTH * 2;
		this.otherPlayersName = otherPlayersName;
		this.firstPlayer = tradeHistory[startPosition + 6];
		this.firstEvent = tradeHistory[startPosition + 7];
		// If it's a closed event, we have to consider the response too
		if (startPosition + Contact.TRADE_HISTORY_ENTRY_LENGTH * 2 <= tradeHistory.length) {
			responseDate = new Date(tradeHistory, startPosition + Contact.TRADE_HISTORY_ENTRY_LENGTH);
			response = tradeHistory[startPosition + Contact.TRADE_HISTORY_ENTRY_LENGTH + 7];
		} else {
			responseDate = null;
			response = Contact.HISTORY_INVALID;
		}
		if (firstPlayer == viewer) {
			switch (firstEvent) {
				case Contact.HISTORY_INVITE_AND_COOP: {
					switch (response) {
						case Contact.HISTORY_ACCEPT_AND_COOP:
							outcome = Outcome.I_STARTED_BOTH_COOPERATE;
							break;
						case Contact.HISTORY_ACCEPT_AND_DEFECT:
							outcome = Outcome.I_STARTED_HE_DEFECTS;
							break;
						case Contact.HISTORY_REJECT:
							outcome = Outcome.HE_REJECTS;
							break;
						default:
							outcome = Outcome.I_INVITE;
							break;
					}
					break;
				}
				case Contact.HISTORY_INVITE_AND_DEFECT: {
					switch (response) {
						case Contact.HISTORY_ACCEPT_AND_COOP:
							outcome = Outcome.I_STARTED_I_DEFECT;
							break;
						case Contact.HISTORY_ACCEPT_AND_DEFECT:
							outcome = Outcome.I_STARTED_BOTH_DEFECT;
							break;
						case Contact.HISTORY_REJECT:
							outcome = Outcome.HE_REJECTS;
							break;
						default:
							outcome = Outcome.I_INVITE;
							break;
					}
					break;
				}
				default: {
					outcome = Outcome.INVALID;
				}
			}
		} else {
			switch (firstEvent) {
				case Contact.HISTORY_INVITE_AND_COOP: {
					switch (response) {
						case Contact.HISTORY_ACCEPT_AND_COOP:
							outcome = Outcome.HE_STARTED_BOTH_COOPERATE;
							break;
						case Contact.HISTORY_ACCEPT_AND_DEFECT:
							outcome = Outcome.HE_STARTED_I_DEFECT;
							break;
						case Contact.HISTORY_REJECT:
							outcome = Outcome.I_REJECT;
							break;
						default:
							outcome = Outcome.HE_INVITES;
							break;
					}
					break;
				}
				case Contact.HISTORY_INVITE_AND_DEFECT: {
					switch (response) {
						case Contact.HISTORY_ACCEPT_AND_COOP:
							outcome = Outcome.HE_STARTED_HE_DEFECTS;
							break;
						case Contact.HISTORY_ACCEPT_AND_DEFECT:
							outcome = Outcome.HE_STARTED_BOTH_DEFECT;
							break;
						case Contact.HISTORY_REJECT:
							outcome = Outcome.I_REJECT;
							break;
						case Contact.HISTORY_INVALID:
							outcome = Outcome.I_INVITE;
							break;
					}
					break;
				}
				default: {
					outcome = Outcome.INVALID;
				}
			}
		}
	}
		
	public int getPointValue(){
		return outcome.getPointValue();
	}

	public int getUsedEnergy(Date currentDate){
		return outcome.getUsedEnergy(currentDate, date, responseDate);
	}
	
	public Date getDate(){
		return date;
	}

	/**
	 * @param timeDiff Display time minus data time.
	 * @param calculator
	 * @return
	 */
	public String getString(long timeDiff, DayCaclulator calculator) {
		String base = "";
		String playerName = otherPlayersName;
		Date eventDate = date;
		if (responseDate != null) {
			eventDate = responseDate;
		}
		if (calculator != null) {
			java.util.Date dataDate = calculator.toDate(eventDate);
			java.util.Date localDate = new java.util.Date(dataDate.getTime() + timeDiff);
			eventDate = calculator.fromDate(localDate);
		}
		return base + eventDate.toString() + " - " + outcome.generateString(playerName) + ((getPointValue()==0)?"":((getPointValue()>0)?" (+$" + getPointValue():" (-$" + (-1 * getPointValue())) + ")");
	}
	
	@Override
	public String toString() {
		return getString(0, null);
	}
}
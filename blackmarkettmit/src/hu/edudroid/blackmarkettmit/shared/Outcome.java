package hu.edudroid.blackmarkettmit.shared;

enum Outcome {
	I_STARTED_BOTH_COOPERATE(Contact.POINT_VALUE_BOTH_COOPERATE, Contact.ENERGY_CONSUMPTION_INVITE, 0),
	I_STARTED_BOTH_DEFECT(Contact.POINT_VALUE_BOTH_DEFECT, Contact.ENERGY_CONSUMPTION_INVITE, 0),
	I_STARTED_I_DEFECT(Contact.POINT_VALUE_I_DEFECT, Contact.ENERGY_CONSUMPTION_INVITE, 0),
	I_STARTED_HE_DEFECTS(Contact.POINT_VALUE_HE_DEFECTS, Contact.ENERGY_CONSUMPTION_INVITE, 0),
	I_REJECT(0, 0, 0),
	HE_REJECTS(0, Contact.ENERGY_CONSUMPTION_INVITE, 0),
	I_INVITE(0, Contact.ENERGY_CONSUMPTION_INVITE, 0),
	HE_INVITES(0, Contact.ENERGY_CONSUMPTION_INVITE, 0),
	HE_STARTED_BOTH_COOPERATE(Contact.POINT_VALUE_BOTH_COOPERATE, 0, Contact.ENERGY_CONSUMPTION_ACCEPT),
	HE_STARTED_BOTH_DEFECT(Contact.POINT_VALUE_BOTH_DEFECT, 0, Contact.ENERGY_CONSUMPTION_ACCEPT),
	HE_STARTED_I_DEFECT(Contact.POINT_VALUE_I_DEFECT, 0, Contact.ENERGY_CONSUMPTION_ACCEPT),
	HE_STARTED_HE_DEFECTS(Contact.POINT_VALUE_HE_DEFECTS, 0, Contact.ENERGY_CONSUMPTION_ACCEPT),
	INVALID(0, 0, 0);
	private final int pointValue;
	private final int firstEventConsumption;
	private final int secondEventConsumption;
	
	private Outcome(int pointValue, int firstEventConsumption, int secondEventConsumption){
		this.pointValue = pointValue;
		this.firstEventConsumption = firstEventConsumption;
		this.secondEventConsumption = secondEventConsumption;
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
			return playerName + " rejected your invitation.";
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

	public int getPointValue() {
		return pointValue;
	}

	public int getUsedEnergy(Date currentDate, Date firstDate, Date responseDate) {
		int consumption = 0;
		if (currentDate.sameDay(firstDate)) {
			consumption += firstEventConsumption; 
		}
		if (currentDate.sameDay(responseDate)) {
			consumption += secondEventConsumption; 
		}
		return consumption;
	}
}
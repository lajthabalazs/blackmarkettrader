package hu.edudroid.blackmarkettmit.shared;

enum Outcome {
	I_STARTED_BOTH_COOPERATE(0, 40),
	I_STARTED_BOTH_DEFECT(0, -10),
	I_STARTED_I_DEFECT(0, 100),
	I_STARTED_HE_DEFECTS(0, -40),
	I_REJECT(Contact.ENERGY_CONSUMPTION_REJECT, 0),
	HE_REJECTS(0, 0),
	I_INVITE(Contact.ENERGY_CONSUMPTION_INVITE, 0),
	HE_INVITES(0, 0),
	HE_STARTED_BOTH_COOPERATE(Contact.ENERGY_CONSUMPTION_ACCEPT, 40),
	HE_STARTED_BOTH_DEFECT(Contact.ENERGY_CONSUMPTION_ACCEPT, -10),
	HE_STARTED_I_DEFECT(Contact.ENERGY_CONSUMPTION_ACCEPT, 100),
	HE_STARTED_HE_DEFECTS(Contact.ENERGY_CONSUMPTION_ACCEPT, -40);
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
}
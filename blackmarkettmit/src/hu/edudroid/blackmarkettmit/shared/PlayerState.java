package hu.edudroid.blackmarkettmit.shared;

public enum PlayerState {
	NEW(1), INVITED_HIM(3), INVITED_ME(0), NEUTRAL(2);
	private Integer value;
	private PlayerState(int value) {
		this.value = value;
	}
	public Integer getValue(){
		return value;
	}
}

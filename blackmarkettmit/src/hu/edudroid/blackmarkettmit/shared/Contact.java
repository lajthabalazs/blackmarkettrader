package hu.edudroid.blackmarkettmit.shared;

import java.io.Serializable;

import hu.edudroid.blackmarkettmit.shared.PlayerState;

public class Contact implements Serializable{

	private static final long serialVersionUID = 3488387526075082543L;
	private int viewer;
	private String firstPlayerKey;
	private String secondPlayerKey;
	private String firstDisplayName;
	private String secondDisplayName;
	private int gameCount = 0;
	private int cooperationCount = 0;
	private int bothDefectCount = 0;
	private int firstDefectCount = 0;
	private int secondDefectCount = 0;
	
	private PlayerState state = PlayerState.NEW; // State is not persisted, it is calculated from events and stats
	
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

	public int getGameCount() {
		return gameCount;
	}

	public void setGameCount(int gameCount) {
		this.gameCount = gameCount;
	}

	public int getCooperationCount() {
		return cooperationCount;
	}

	public void setCooperationCount(int cooperationCount) {
		this.cooperationCount = cooperationCount;
	}

	public int getBothDefectCount() {
		return bothDefectCount;
	}

	public void setBothDefectCount(int bothDefectCount) {
		this.bothDefectCount = bothDefectCount;
	}

	public int getFirstDefectCount() {
		return firstDefectCount;
	}

	public void setFirstDefectCount(int firstDefectCount) {
		this.firstDefectCount = firstDefectCount;
	}

	public int getSecondDefectCount() {
		return secondDefectCount;
	}

	public void setSecondDefectCount(int secondDefectCount) {
		this.secondDefectCount = secondDefectCount;
	}

	public PlayerState getState() {
		return state;
	}

	public void setState(PlayerState state) {
		this.state = state;
	}
}

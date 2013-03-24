package hu.edudroid.blackmarkettmit.shared;

import java.io.Serializable;
import java.util.Date;

import hu.edudroid.blackmarkettmit.shared.PlayerState;

public class Contact implements Serializable{

	private static final long serialVersionUID = 3488387526075082543L;
	public static final int CHOICE_COOPERATE = 0;
	public static final int CHOICE_DEFECT = 1;
	public static final int CHOICE_REJECT = -1;
	public static final int CHOICE_NONE = -2;

	private String entityKey;
	
	private int viewer;
	// Active game
	private int inGame;
	private int whoStarted;
	private int firstPlayerChoice;
	private int secondPlayerChoice;
	private Date gameStart;
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
	private int gameCount = 0;
	private int cooperationCount = 0;
	private int bothDefectCount = 0;
	private int firstDefectCount = 0;
	private int secondDefectCount = 0;
	
	private PlayerState state = PlayerState.NEW; // State is not persisted, it is calculated from events and statistics
	
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

	public int getInGame() {
		return inGame;
	}

	public void setInGame(int inGame) {
		this.inGame = inGame;
	}

	public int getWhoStarted() {
		return whoStarted;
	}

	public void setWhoStarted(int whoStarted) {
		this.whoStarted = whoStarted;
	}

	public int getFirstPlayerChoice() {
		return firstPlayerChoice;
	}

	public void setFirstPlayerChoice(int firstPlayerChoice) {
		this.firstPlayerChoice = firstPlayerChoice;
	}

	public int getSecondPlayerChoice() {
		return secondPlayerChoice;
	}

	public void setSecondPlayerChoice(int secondPlayerChoice) {
		this.secondPlayerChoice = secondPlayerChoice;
	}

	public Date getGameStart() {
		return gameStart;
	}

	public void setGameStart(Date gameStart) {
		this.gameStart = gameStart;
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

package hu.edudroid.blackmarkettmit.shared;

import java.util.Comparator;

public class Player {
	private long id;
	private String displayName;
	private PlayerState state;
	private int gameCount;
	private int cooperationCount;
	private int screwedMeCount;
	private int screwedHimCount;
	private int bothScrevedCount;
	private static Comparator<Player> comparator = new Comparator<Player>() {
		
		@Override
		public int compare(Player o1, Player o2) {
			if (o1.getState().getValue() < o2.getState().getValue()) {
				return -1;
			} else if (o1.getState().getValue() > o2.getState().getValue()) {
				return 1;
			}
			return o1.getDisplayName().compareTo(o2.getDisplayName());
		}
	};
	
	public Player(int id, String displayName) {
		this.id = id;
		this.displayName = displayName;
		double choice = Math.random();
		state = (choice<0.25)?PlayerState.NEW:((choice<0.5)?PlayerState.NEUTRAL:((choice<0.75)?PlayerState.INVITED_ME:PlayerState.INVITED_HIM));
		if (state != PlayerState.NEW) {
			cooperationCount = (int)(Math.random() * 100);
			screwedHimCount = (int)(Math.random() * 100);
			screwedMeCount = (int)(Math.random() * 100);
			bothScrevedCount = (int)(Math.random() * 100);
			gameCount = cooperationCount + screwedHimCount + screwedMeCount + bothScrevedCount; 
		} else {
			cooperationCount = 0;
			screwedHimCount = 0;
			screwedMeCount = 0;
			bothScrevedCount = 0;
			gameCount = 0;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public PlayerState getState() {
		return state;
	}

	public void setState(PlayerState state) {
		this.state = state;
	}

	public int getCooperationCount() {
		return cooperationCount;
	}

	public void setCooperationCount(int cooperationCount) {
		this.cooperationCount = cooperationCount;
	}

	public int getScrewedMeCount() {
		return screwedMeCount;
	}

	public void setScrewedMeCount(int screwedMeCount) {
		this.screwedMeCount = screwedMeCount;
	}

	public int getScrewedHimCount() {
		return screwedHimCount;
	}

	public void setScrewedHimCount(int screwedHimCount) {
		this.screwedHimCount = screwedHimCount;
	}

	public int getBothScrevedCount() {
		return bothScrevedCount;
	}

	public void setBothScrevedCount(int bothScrevedCount) {
		this.bothScrevedCount = bothScrevedCount;
	}

	public int getGameCount() {
		return gameCount;
	}

	public void setGameCount(int gameCount) {
		this.gameCount = gameCount;
	}

	public static Comparator<Player> getComparator() {
		return comparator;
	}
}

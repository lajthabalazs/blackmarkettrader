package hu.edudroid.blackmarkettmit.shared;

import java.util.Comparator;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Contact {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long contactId;
	@Persistent
	private long playerId;
	@Persistent
	private String displayName;
	@Persistent
	private int gameCount;
	@Persistent
	private int cooperationCount;
	@Persistent
	private int screwedMeCount;
	@Persistent
	private int screwedHimCount;
	@Persistent
	private int bothScrevedCount;
	
	private PlayerState state; // State is not persisted, it is calculated from events and stats

	private static Comparator<Contact> comparator = new Comparator<Contact>() {
		@Override
		public int compare(Contact o1, Contact o2) {
			if (o1.getState().getValue() < o2.getState().getValue()) {
				return -1;
			} else if (o1.getState().getValue() > o2.getState().getValue()) {
				return 1;
			}
			return o1.getDisplayName().compareTo(o2.getDisplayName());
		}
	};

	public Contact() {}
	public Contact(int id, String displayName) {
		this.playerId = id;
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

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long id) {
		this.playerId = id;
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

	public static Comparator<Contact> getComparator() {
		return comparator;
	}
}

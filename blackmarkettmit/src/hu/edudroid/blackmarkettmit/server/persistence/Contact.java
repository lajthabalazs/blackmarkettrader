package hu.edudroid.blackmarkettmit.server.persistence;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Contact {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key contactKey;
	@Persistent
	@Unowned
	private BlackMarketUser player;
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
	
	public BlackMarketUser getPlayer() {
		return player;
	}

	public void setPlayer(BlackMarketUser player) {
		this.player = player;
	}


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
}

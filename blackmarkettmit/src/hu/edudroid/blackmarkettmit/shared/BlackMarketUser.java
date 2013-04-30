package hu.edudroid.blackmarkettmit.shared;

import java.io.Serializable;

public class BlackMarketUser implements Serializable{

	private static final long serialVersionUID = -8822523082905084208L;
	private static final int MAX_ENERGY = 90;
	private String entityKey;
	private String externalId;
	private String userName;
	private float random;
	private byte[] loginDates;
	
	public BlackMarketUser() {
		
	}

	public String getEntityKey() {
		return entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public float getRandom() {
		return random;
	}
	public void setRandom(float random) {
		this.random = random;
	}
	
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}

	public byte[] getLoginDates() {
		if (loginDates == null) {
			loginDates = new byte[0];
		}
		return loginDates;
	}

	public void setLoginDates(byte[] loginDates) {
		this.loginDates = loginDates;
	}
}

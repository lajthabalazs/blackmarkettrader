package hu.edudroid.blackmarkettmit.shared;

import java.io.Serializable;

public class BlackMarketUser implements Serializable{

	private static final long serialVersionUID = -8822523082905084208L;
	private static final int MAX_ENERGY = 90;
	
	public static final String GENDER_MALE = "male";
	public static final String GENDER_FEMALE = "female";
	public static final String GENDER_UNKNOWN = "unknown";
	
	public static final int LOGIN_HISTORY_ENTRY_LENGTH = 7;	
	
	private String entityKey;
	private String externalId;
	private String userName;
	private String emailAddress;
	private String gender;
	private String birthday;
	private float random;
	private byte[] loginDates;
	private long lastNotificationView = -1;
	private long lastRewardView = -1;
	
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
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
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

	public long getLastNotificationView() {
		return lastNotificationView;
	}

	public void setLastNotificationView(long lastNotificationView) {
		this.lastNotificationView = lastNotificationView;
	}

	public long getLastRewardView() {
		return lastRewardView;
	}

	public void setLastRewardView(long lastRewardView) {
		this.lastRewardView = lastRewardView;
	}
}
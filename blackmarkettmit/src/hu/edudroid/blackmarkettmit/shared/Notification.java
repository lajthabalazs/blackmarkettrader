package hu.edudroid.blackmarkettmit.shared;

public class Notification {
	public enum Component{}
	
	private long time;
	private String message;
	private Component component;
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}

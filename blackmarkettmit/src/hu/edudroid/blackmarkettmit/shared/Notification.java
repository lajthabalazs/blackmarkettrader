package hu.edudroid.blackmarkettmit.shared;

public class Notification implements Comparable<Notification>{
	public enum Component{
		LOGIN_BOX;
	}
	
	private long time;
	private String title;
	private String message;
	private Component component;
	
	public Notification(Component component, String title, String message, long time) {
		this.time = time;
		this.title = title;
		this.message = message;
		this.component = component;
	}
	
	public long getTime() {
		return time;
	}
	public String getTitle() {
		return title;
	}
	public String getMessage() {
		return message;
	}
	public Component getComponent() {
		return component;
	}

	@Override
	public int compareTo(Notification o) {
		return new Long(time).compareTo(o.getTime());
	}
}

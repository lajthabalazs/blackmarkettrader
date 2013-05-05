package hu.edudroid.blackmarkettmit.shared;

import java.util.Comparator;


public class Event {
	public static final Comparator<Event> EVENT_COMPARATOR = new Comparator<Event>() {

		@Override
		public int compare(Event e1, Event e2) {
			return (-1) * e1.date.compareTo(e2.date);
		}
	};
	
	protected Date date;

	public Event(byte[] data, int datePosition) {
		date = new Date(data, datePosition);		
	}
	
	
	public int getUsedEnergy() {
		return 0;
	}
	
	public int getScore() {
		return 0;
	}
}

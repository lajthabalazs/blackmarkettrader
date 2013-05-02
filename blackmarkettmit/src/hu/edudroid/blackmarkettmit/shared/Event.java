package hu.edudroid.blackmarkettmit.shared;

import java.util.Comparator;


public class Event {
	public static final Comparator<Event> EVENT_COMPARATOR = new Comparator<Event>() {

		@Override
		public int compare(Event e1, Event e2) {
			return (-1) * e1.dateString.compareTo(e2.dateString);
		}
	};
	
	public Event(byte[] data, int datePosition) {
		dateString = "" + (data[datePosition] + Contact.START_YEAR);
		dateString = dateString + "-" + (data[datePosition + 1]<10?"0":"") + (data[datePosition + 1] + 1);
		dateString = dateString + "-" + (data[datePosition + 2]<10?"0":"") + (data[datePosition + 2] + 1);
		dateString = dateString + " " + (data[datePosition + 3]<10?"0":"") + data[datePosition + 3];
		dateString = dateString + ":" + (data[datePosition + 4]<10?"0":"") + data[datePosition + 4];
		dateString = dateString + ":" + (data[datePosition + 5]<10?"0":"") + data[datePosition + 5];
		date = new Date((data[datePosition] + Contact.START_YEAR), data[datePosition + 1], data[datePosition + 2]);		
	}
	
	protected Date date;
	protected String dateString;
}

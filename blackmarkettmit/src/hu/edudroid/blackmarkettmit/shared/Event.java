package hu.edudroid.blackmarkettmit.shared;

import java.util.Comparator;

import com.google.gwt.i18n.client.DateTimeFormat;

public class Event {
	public static final Comparator<Event> EVENT_COMPARATOR = new Comparator<Event>() {

		@Override
		public int compare(Event e1, Event e2) {
			return (-1) * e1.dateString.compareTo(e2.dateString);
		}
	};
	
	public Event(byte[] data, int startPosition) {
		dateString = "" + (data[startPosition] + Contact.START_YEAR);
		dateString = dateString + "-" + (data[startPosition + 1]<10?"0":"") + data[startPosition + 1];
		dateString = dateString + "-" + (data[startPosition + 2]<10?"0":"") + data[startPosition + 2];
		dateString = dateString + " " + (data[startPosition + 3]<10?"0":"") + data[startPosition + 3];
		dateString = dateString + ":" + (data[startPosition + 4]<10?"0":"") + data[startPosition + 4];
		dateString = dateString + ":" + (data[startPosition + 5]<10?"0":"") + data[startPosition + 5];
		date = new Date((data[startPosition] + Contact.START_YEAR), data[startPosition + 1], data[startPosition + 2]);		
	}
	
	protected Date date;
	protected String dateString;
	
	/**
	 * A simple date class containing only year, month and day
	 * @author lajthabalazs
	 *
	 */
	public static class Date {
		public final int year;
		public final int month;
		public final int day;
		public final int hash;
		
		public Date(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
			this.hash = year * 10000 + month + day;
		}

		public Date() {
			java.util.Date now = new java.util.Date();
			year = Integer.parseInt(DateTimeFormat.getFormat( "y" ).format(now));
			month = Integer.parseInt(DateTimeFormat.getFormat( "M" ).format(now)) - 1;
			day = Integer.parseInt(DateTimeFormat.getFormat( "d" ).format(now));
			this.hash = year * 10000 + month + day;
		}

		@Override
		public String toString() {
			return year + "-" + month + "-" + day;
		}
		
		@Override
		public int hashCode() {
			return hash;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (! (obj instanceof Date)) {
				return false;
			}
			Date other = (Date) obj;
			return (this.year == other.year) && (this.month == other.month) && (this.day == other.day);
		}
	}
}

package hu.edudroid.blackmarkettmit.shared;

/**
 * A simple date class containing only year, month and day
 * @author lajthabalazs
 *
 */
public class Date implements Comparable<Date>{
	public final int year;
	public final int month;
	public final int day;
	public final int hour;
	public final int minute;
	public final int sec;
	public final int hash;
	
	public Date(int year, int month, int day, int hour, int minute, int sec) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.sec = sec;
		this.hash = year * 10000 + month * 100 + day;
	}

	public Date(byte[] loginHistory, int startPosition) {
		this(loginHistory[startPosition] + Contact.START_YEAR,
				loginHistory[startPosition + 1],
				loginHistory[startPosition + 2],
				loginHistory[startPosition + 3],
				loginHistory[startPosition + 4],
				loginHistory[startPosition + 5]);
	}
	
	@Override
	public String toString() {
		return (year<10?"0":"") + year + "-"+ (month + 1<10?"0":"") + (month + 1) + "-" + (day + 1<10?"0":"") + (day + 1);
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
		return (this.year == other.year) &&
				(this.month == other.month) &&
				(this.day == other.day) &&
				(this.hour == other.hour) &&
				(this.minute == other.minute) &&
				(this.sec == other.sec);
	}

	@Override
	public int compareTo(Date other) {
		if (other == null) {
			return -1;
		}
		return toString().compareTo(other.toString());
	}

	public boolean sameDay(Date otherDate) {
		return (year == otherDate.year) && (month == otherDate.month) && (day == otherDate.day);
	}
}
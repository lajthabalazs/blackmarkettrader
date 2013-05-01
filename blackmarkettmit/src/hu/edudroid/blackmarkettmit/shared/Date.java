package hu.edudroid.blackmarkettmit.shared;

/**
 * A simple date class containing only year, month and day
 * @author lajthabalazs
 *
 */
public class Date {
	public final int year;
	public final int month;
	public final int day;
	public final int hash;
	
	public Date(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hash = year * 10000 + month * 100 + day;
	}

	public Date(byte[] loginHistory, int startPosition) {
		this(loginHistory[startPosition] + Contact.START_YEAR,
				loginHistory[startPosition + 1],
				loginHistory[startPosition + 2]);
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
		return (this.year == other.year) && (this.month == other.month) && (this.day == other.day);
	}
}
package hu.edudroid.blackmarkettmit.shared;

public interface DayCaclulator {

	int getDaysBetween(java.util.Date startDate, java.util.Date endDate);

	Date fromDate(java.util.Date date);

	java.util.Date toDate(Date date);

	int getDaysBetween(Date startDate, Date endDate);

}

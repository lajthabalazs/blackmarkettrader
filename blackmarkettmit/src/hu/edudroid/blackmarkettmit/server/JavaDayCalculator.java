package hu.edudroid.blackmarkettmit.server;

import java.util.Calendar;
import java.util.Date;

import hu.edudroid.blackmarkettmit.shared.DayCaclulator;

public class JavaDayCalculator implements DayCaclulator {
	
	public static final DayCaclulator DAY_CALCULATOR = new JavaDayCalculator();
	
	Calendar start = Calendar.getInstance();
	Calendar end = Calendar.getInstance();
	
	private JavaDayCalculator() {}
	@Override
	public int getDaysBetween(Date startDate, Date endDate) {
		start.setTime(startDate);
		end.setTime(endDate);
		// Don't bother with timezones!
		long startDay = startDate.getTime()/(24l * 3600l * 1000l);
		long endDay = endDate.getTime()/(24l * 3600l * 1000l);
		return (int)(endDay - startDay);
	}
	@Override
	public hu.edudroid.blackmarkettmit.shared.Date fromDate(Date date) {
		start.setTime(date);
		return new hu.edudroid.blackmarkettmit.shared.Date(start.get(Calendar.YEAR),
				start.get(Calendar.MONTH),
				start.get(Calendar.DAY_OF_MONTH) - 1,
				start.get(Calendar.HOUR_OF_DAY),
				start.get(Calendar.MINUTE),
				start.get(Calendar.SECOND)
				);
	}
	@Override
	public Date toDate(hu.edudroid.blackmarkettmit.shared.Date date) {
		start.set(Calendar.YEAR, date.year);
		start.set(Calendar.MONTH, date.month);
		start.set(Calendar.DAY_OF_MONTH, date.day + 1);
		start.set(Calendar.HOUR_OF_DAY, date.hour);
		start.set(Calendar.MINUTE, date.minute);
		start.set(Calendar.SECOND, date.sec);
		return start.getTime();
	}
	@Override
	public int getDaysBetween(hu.edudroid.blackmarkettmit.shared.Date startDate,
			hu.edudroid.blackmarkettmit.shared.Date endDate) {
		return getDaysBetween(toDate(startDate), toDate(endDate));
	}
}

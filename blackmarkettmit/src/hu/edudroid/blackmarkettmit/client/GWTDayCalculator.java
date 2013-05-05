package hu.edudroid.blackmarkettmit.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import hu.edudroid.blackmarkettmit.shared.DayCaclulator;

public class GWTDayCalculator implements DayCaclulator {
	
	public static final DayCaclulator DAY_CALCULATOR = new GWTDayCalculator();

	private GWTDayCalculator(){}
	
	@Override
	public int getDaysBetween(Date startDate, Date endDate) {
		return CalendarUtil.getDaysBetween(startDate, endDate);
	}

	@Override
	public hu.edudroid.blackmarkettmit.shared.Date fromDate(Date date) {
		return new hu.edudroid.blackmarkettmit.shared.Date(Integer.parseInt(DateTimeFormat.getFormat( "y" ).format(date)),
				Integer.parseInt(DateTimeFormat.getFormat( "M" ).format(date)) - 1,
				Integer.parseInt(DateTimeFormat.getFormat( "d" ).format(date)) - 1,
				Integer.parseInt(DateTimeFormat.getFormat( "H" ).format(date)),
				Integer.parseInt(DateTimeFormat.getFormat( "m" ).format(date)),
				Integer.parseInt(DateTimeFormat.getFormat( "s" ).format(date)));
	}

	@Override
	public Date toDate(hu.edudroid.blackmarkettmit.shared.Date date) {
		return DateTimeFormat.getFormat("yyyy-MM-dd").parse(date.year + "-" + (date.month + 1) + "-" + (date.day + 1));
	}

	@Override
	public int getDaysBetween(hu.edudroid.blackmarkettmit.shared.Date startDate,
			hu.edudroid.blackmarkettmit.shared.Date endDate) {
		return getDaysBetween(toDate(startDate), toDate(endDate));
	}


}

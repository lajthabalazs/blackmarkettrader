package hu.edudroid.blackmarkettmit.shared;

import java.util.Comparator;

public class ContactComparator implements Comparator<Contact> {

	@Override
	public int compare(Contact o1, Contact o2) {
		if (o1.getState().getValue() < o2.getState().getValue()) {
			return -1;
		} else if (o1.getState().getValue() > o2.getState().getValue()) {
			return 1;
		}
		int firstValue = 0;
		if (o1.getViewer() == 0) {
			firstValue = o1.getCooperationCount() + o1.getFirstDefectCount() - o1.getBothDefectCount() - o1.getSecondDefectCount(); 
		} else {
			firstValue = o1.getCooperationCount() + o1.getSecondDefectCount() - o1.getBothDefectCount() - o1.getFirstDefectCount();
		}
		int secondValue = 0;
		if (o2.getViewer() == 0) {
			secondValue = o2.getCooperationCount() + o2.getSecondDefectCount() - o2.getBothDefectCount() - o2.getFirstDefectCount();
		} else {
			secondValue = o2.getCooperationCount() + o2.getFirstDefectCount() - o2.getBothDefectCount() - o2.getSecondDefectCount();
		}
		if (firstValue < secondValue){
			return -1;
		} else if (firstValue > secondValue) {
			return 1;
		} else {
			return 0;
		}
	}
}

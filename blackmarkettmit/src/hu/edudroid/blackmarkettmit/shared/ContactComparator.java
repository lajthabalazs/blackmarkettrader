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
		// Order based on player display name
		String o1DisplayName = (o1.getViewer() == 0)?o1.getSecondDisplayName():o1.getFirstDisplayName();
		String o2DisplayName = (o2.getViewer() == 0)?o2.getSecondDisplayName():o2.getFirstDisplayName();
		return o1DisplayName.compareToIgnoreCase(o2DisplayName);
	}
}

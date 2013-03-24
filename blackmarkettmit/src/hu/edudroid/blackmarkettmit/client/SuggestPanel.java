package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class SuggestPanel extends FlowPanel {
	public SuggestPanel(Contact contact, SuggestListener listener) {
		super();
		String displayName = null;
		if (contact.getViewer() == 0) {
			displayName = contact.getSecondDebugDisplayName();
		} else {
			displayName = contact.getFirstDebugDisplayName();
		}
		this.add(new Label(displayName + " is asking for a contact to trade with."));
		this.add(new SuggestButton(contact, listener));
	}
}

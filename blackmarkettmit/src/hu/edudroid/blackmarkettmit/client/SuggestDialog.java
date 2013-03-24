package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SuggestDialog extends DialogBox implements ClickHandler {

	public SuggestDialog(Contact contact, List<Contact> result, SuggestListener listener) {
		super();
		if (contact.getViewer() == 0) {
			setText("Suggesting contact for " + contact.getSecondDebugDisplayName());
		} else {
			setText("Suggesting contact for " + contact.getFirstDebugDisplayName());
		}
		VerticalPanel mainPanel = new VerticalPanel();
		for (Contact suggestableContact : result) {
			HorizontalPanel panel = new HorizontalPanel();
			if (suggestableContact.getViewer() == 0) {
				panel.add(new Label(suggestableContact.getSecondDebugDisplayName()));
			} else {
				panel.add(new Label(suggestableContact.getFirstDebugDisplayName()));
			}
			panel.add(new SendSuggestionButton(contact, suggestableContact, listener));
			mainPanel.add(panel);
		}
		Button cancelButton = new Button("Maybe another time");
		cancelButton.addClickHandler(this);
		mainPanel.add(cancelButton);
		setWidget(mainPanel);
		show();
	}

	@Override
	public void onClick(ClickEvent event) {
		hide();
	}
}

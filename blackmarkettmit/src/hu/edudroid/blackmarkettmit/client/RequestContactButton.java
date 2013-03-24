package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.client.GetContactDialog.GetContactDialogListener;
import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class RequestContactButton extends Button implements ClickHandler {

	private GetContactDialogListener listener;
	private Contact contact;

	public RequestContactButton(Contact contact, GetContactDialogListener listener) {
		super("Ask for contact");
		this.contact = contact;
		this.listener = listener;
		super.addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {		
		listener.requestContactFromPlayer(contact);
	}
}

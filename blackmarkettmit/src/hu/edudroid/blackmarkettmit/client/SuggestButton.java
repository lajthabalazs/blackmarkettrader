package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class SuggestButton extends Button implements ClickHandler{

	private SuggestListener listener;
	private Contact contact;

	public SuggestButton(Contact player, SuggestListener listener) {
		super("Suggest");
		super.addClickHandler(this);
		this.contact = player;
		this.listener = listener;
	}

	@Override
	public void onClick(ClickEvent event) {
		listener.userWantsToSuggestForContact(contact);
	}
}

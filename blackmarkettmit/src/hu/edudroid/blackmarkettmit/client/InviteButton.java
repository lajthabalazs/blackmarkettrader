package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class InviteButton extends Button implements ClickHandler{
	private Contact player;
	private TradeActionHandler listener;
	
	public InviteButton(Contact player, TradeActionHandler listener) {
		super("Invite (" + Contact.ENERGY_CONSUMPTION_INVITE + "E)");
		this.player = player;
		this.listener = listener;
		addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		new InviteDialog(player, listener);
	}
}

package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class RejectButton extends Button implements ClickHandler{
	private Contact player;
	private TradeActionHandler listener;
	
	public RejectButton(Contact player, TradeActionHandler listener) {
		super("Reject");
		this.player = player;
		this.listener = listener;
		addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		listener.reject(player);
	}
}

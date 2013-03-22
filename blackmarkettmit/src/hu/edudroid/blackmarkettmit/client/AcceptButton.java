package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Player;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class AcceptButton extends Button implements ClickHandler{
	private Player player;
	private TradeActionHandler listener;
	
	public AcceptButton(Player player, TradeActionHandler listener) {
		super("Accept");
		this.player = player;
		this.listener = listener;
		addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		new AcceptDialog(player, listener);
	}
}

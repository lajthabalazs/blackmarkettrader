package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AcceptDialog extends DialogBox implements ClickHandler {

	private Button screwButton;
	private Button cooperateButton;
	private Button cancelButton;
	private TradeActionHandler listener;
	private Contact player;

	public AcceptDialog(Contact player, TradeActionHandler listener) {
		this.listener = listener;
		this.player = player;
		VerticalPanel mainPanel = new VerticalPanel();
		setText("Accept " + player.getDisplayName() + "'s invitation");
		Label label = new Label("You were invited to trade by " + player.getDisplayName() + "! What will you do?");
		screwButton = new Button("Screw him/her over");
		screwButton.addClickHandler(this);
		cooperateButton = new Button("Let's be friendly");
		cooperateButton.addClickHandler(this);
		cancelButton = new Button("Let me think this over");
		cancelButton.addClickHandler(this);
		mainPanel.add(label);
		mainPanel.add(cooperateButton);
		mainPanel.add(screwButton);
		mainPanel.add(cancelButton);
		setWidget(mainPanel);
		show();
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(cooperateButton)){
			listener.cooperate(player);
		} else if (event.getSource().equals(screwButton)) {
			listener.screw(player);
		}
		hide();
	}
}

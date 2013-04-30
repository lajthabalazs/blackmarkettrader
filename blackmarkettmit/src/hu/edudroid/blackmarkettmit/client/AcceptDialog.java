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
		Label label;
		String playerName;
		if (player.getViewer() == 0) {
			playerName = player.getSecondDisplayName();
		} else {
			playerName = player.getFirstDisplayName();
		}
		setText("Accept " + playerName + "'s invitation");
		label = new Label(playerName + " want's to buy a painting! Do you have something?");
		cooperateButton = new Button("Sell the real deal");
		cooperateButton.addClickHandler(this);
		screwButton = new Button("Let's sell a fake");
		screwButton.addClickHandler(this);
		cancelButton = new Button("I have to think about it");
		cancelButton.addClickHandler(this);
		mainPanel.add(label);
		mainPanel.add(cooperateButton);
		mainPanel.add(screwButton);
		mainPanel.add(cancelButton);
		setWidget(mainPanel);
		center();		
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

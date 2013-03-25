package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InviteDialog extends DialogBox implements ClickHandler {

	private Button screwButton;
	private Button cooperateButton;
	private Button cancelButton;
	private TradeActionHandler listener;
	private Contact player;

	public InviteDialog(Contact player, TradeActionHandler listener) {
		this.listener = listener;
		this.player = player;
		VerticalPanel mainPanel = new VerticalPanel();
		String playerName = null;
		if (player.getViewer() == 0) {
			playerName = player.getSecondDebugDisplayName();
		} else {
			playerName = player.getFirstDebugDisplayName();
		}
		setText("Invite " + playerName);
		Label label = new Label("Let's get this started! What will you do once " + playerName + " accepts you invitation?");
		label.setWidth("200px");
		screwButton = new Button("I'll screw him/her over");
		screwButton.addClickHandler(this);
		cooperateButton = new Button("I'll cooperate");
		cooperateButton.addClickHandler(this);
		cancelButton = new Button("Let's reconsider this");
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

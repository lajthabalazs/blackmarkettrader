package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.client.GetContactDialog.GetContactDialogListener;
import hu.edudroid.blackmarkettmit.client.LoginDialog.LoginListener;
import hu.edudroid.blackmarkettmit.shared.Player;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Blackmarkettmit implements EntryPoint, LoginListener, GetContactDialogListener {
	private String userName = null;
	private LoginDialog loginDialog;
	private GetContactDialog getContactDialog;

	public void onModuleLoad() {
		RootPanel waitingText = RootPanel.get("waitingText");
		waitingText.setVisible(false);
		if (userName == null) {
			loginDialog = new LoginDialog(this);
		} else {
			initGame();
		}
	}

	private void initGame() {
		RootPanel appPanel = RootPanel.get("appPanel");
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, new Label("Player name"));
		table.setWidget(0, 1, new Label("Games"));
		table.setWidget(0, 2, new Label("Cooperated"));
		table.setWidget(0, 3, new Label("Both screwed"));
		table.setWidget(0, 4, new Label("I screwed her"));
		table.setWidget(0, 5, new Label("She screwed me"));
		table.setWidget(0, 5, new Label("Action"));
		table.getRowFormatter().addStyleName(0, "headerRow");
		appPanel.add(table);
		
	}

	@Override
	public void login(String userName, String password) {
		loginDialog.hide();
		this.userName = userName;
		initGame();
	}

	@Override
	public void getRandom() {
	}

	@Override
	public void requestContactFromPlayer(Player player) {
	}
}

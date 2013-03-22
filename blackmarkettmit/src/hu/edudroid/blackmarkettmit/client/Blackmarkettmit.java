package hu.edudroid.blackmarkettmit.client;

import java.util.ArrayList;

import hu.edudroid.blackmarkettmit.client.GetContactDialog.GetContactDialogListener;
import hu.edudroid.blackmarkettmit.client.LoginDialog.LoginListener;
import hu.edudroid.blackmarkettmit.shared.Player;
import hu.edudroid.blackmarkettmit.shared.PlayerState;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Blackmarkettmit implements EntryPoint, LoginListener, GetContactDialogListener, ClickHandler, TradeActionHandler {
	private String userName = null;
	private LoginDialog loginDialog;
	private GetContactDialog getContactDialog;
	private Button addContactButton;
	private ArrayList<Player> contactList;
	
	public Blackmarkettmit() {
		contactList = new ArrayList<Player>();
		for (int i = 0; i < 20; i++) {
			contactList.add(new Player(i, "Player " + i));
		}
		// Order contact list
		java.util.Collections.sort(contactList, Player.getComparator());
	}

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
		table.setWidget(0, 3, new Label("Both got screwed"));
		table.setWidget(0, 4, new Label("I got screwed"));
		table.setWidget(0, 5, new Label("I won"));
		table.setWidget(0, 6, new Label("Action"));
		table.getRowFormatter().addStyleName(0, "headerRow");
		for (Player player:contactList) {
			int nextRow = table.getRowCount();
			table.setWidget(nextRow, 0, new Label(player.getDisplayName()));
			table.setWidget(nextRow, 1, new Label(""+ player.getGameCount()));
			table.setWidget(nextRow, 2, new Label(""+ player.getCooperationCount()));
			table.setWidget(nextRow, 3, new Label(""+ player.getBothScrevedCount()));
			table.setWidget(nextRow, 4, new Label(""+ player.getScrewedMeCount()));
			table.setWidget(nextRow, 5, new Label(""+ player.getScrewedHimCount()));
			if (player.getState() == PlayerState.INVITED_HIM) {
				table.setWidget(nextRow, 6, new Label("Waiting response"));
			} else if (player.getState() == PlayerState.INVITED_ME) {
				HorizontalPanel buttonHolder = new HorizontalPanel();
				buttonHolder.add(new AcceptButton(player, this));
				buttonHolder.add(new RejectButton(player, this));
				table.setWidget(nextRow, 6, buttonHolder);
			} else {
				table.setWidget(nextRow, 6, new InviteButton(player, this));
			}
		}
		addContactButton = new Button("Add new contact");
		addContactButton.addClickHandler(this);
		table.setWidth("100%");
		ScrollPanel tableScrollPanel = new ScrollPanel(table);
		tableScrollPanel.setHeight("400px");
		appPanel.add(tableScrollPanel);
		appPanel.add(addContactButton);
	}

	@Override
	public void login(String userName, String password) {
		loginDialog.hide();
		this.userName = userName;
		initGame();
	}

	@Override
	public void getRandom() {
		getContactDialog.hide();
	}

	@Override
	public void requestContactFromPlayer(Player player) {
		getContactDialog.hide();
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(addContactButton)) {
			getContactDialog = new GetContactDialog(contactList, this);
		}
	}

	@Override
	public void cooperate(Player player) {
	}

	@Override
	public void screw(Player player) {
	}

	@Override
	public void reject(Player player) {
	}
}

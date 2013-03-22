package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.client.GetContactDialog.GetContactDialogListener;
import hu.edudroid.blackmarkettmit.shared.LoginInfo;
import hu.edudroid.blackmarkettmit.shared.Player;
import hu.edudroid.blackmarkettmit.shared.PlayerState;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Blackmarkettmit implements EntryPoint, GetContactDialogListener, ClickHandler, TradeActionHandler {
	private GetContactDialog getContactDialog;
	private Button addContactButton;
	private ArrayList<Player> contactList;
	private LoginInfo loginInfo;
	
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	
	public void onModuleLoad() {
		login();
	}
	
	private void login() {
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {

					public void onFailure(Throwable error) {
					}

					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						if (loginInfo.isLoggedIn()) {
							initGame();
						} else {
							loadLogin();
						}
					}
				});
	}

	private void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		VerticalPanel loginPanel = new VerticalPanel();
		loginPanel.add(new Label("Please log in with you google account to play the game!"));
		loginPanel.add(signInLink);
		RootPanel.get("appPanel").add(loginPanel);
	}

	private void initUsers() {
		contactList = new ArrayList<Player>();
		for (int i = 0; i < 20; i++) {
			contactList.add(new Player(i, "Player " + i));
		}
		// Order contact list
		java.util.Collections.sort(contactList, Player.getComparator());
	}
 
	private void initGame() {
		signOutLink.setHref(loginInfo.getLogoutUrl());
		initUsers();
		RootPanel appPanel = RootPanel.get("appPanel");
		FlexTable actionTable = new FlexTable();
		actionTable.setWidget(0, 0, new Label("Player name"));
		actionTable.setWidget(0, 1, new Label("Games"));
		actionTable.setWidget(0, 2, new Label("Co-op"));
		actionTable.setWidget(0, 3, new Label("Defeat"));
		actionTable.setWidget(0, 4, new Label("I lost"));
		actionTable.setWidget(0, 5, new Label("I won"));
		actionTable.setWidget(0, 6, new Label("Action"));
		for (Player player:contactList) {
			int nextRow = actionTable.getRowCount();
			actionTable.setWidget(nextRow, 0, new Label(player.getDisplayName()));
			actionTable.setWidget(nextRow, 1, new Label(""+ player.getGameCount()));
			actionTable.setWidget(nextRow, 2, new Label(""+ player.getCooperationCount()));
			actionTable.setWidget(nextRow, 3, new Label(""+ player.getBothScrevedCount()));
			actionTable.setWidget(nextRow, 4, new Label(""+ player.getScrewedMeCount()));
			actionTable.setWidget(nextRow, 5, new Label(""+ player.getScrewedHimCount()));
			if (player.getState() == PlayerState.INVITED_HIM) {
				actionTable.setWidget(nextRow, 6, new Label("Waiting response"));
			} else if (player.getState() == PlayerState.INVITED_ME) {
				HorizontalPanel buttonHolder = new HorizontalPanel();
				buttonHolder.add(new AcceptButton(player, this));
				buttonHolder.add(new RejectButton(player, this));
				actionTable.setWidget(nextRow, 6, buttonHolder);
			} else {
				actionTable.setWidget(nextRow, 6, new InviteButton(player, this));
			}
		}
		VerticalPanel historyPanel = new VerticalPanel();
		for (int i = 0; i < 100; i++) {
			FlowPanel historyElement = new FlowPanel();
			historyElement.add(new Label("20 minutes ago - "));
			historyElement.add(new Label("Player 4 asked a suggestion."));
			historyElement.add(new SuggestButton(contactList.get(0)));
			historyPanel.add(historyElement);
		}
		addContactButton = new Button("Add new contact");
		addContactButton.addClickHandler(this);
		ScrollPanel actionTableScroll = new ScrollPanel(actionTable);
		actionTableScroll.setHeight("400px");
		ScrollPanel historyScroll = new ScrollPanel(historyPanel);
		historyScroll.setHeight("400px");
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(actionTableScroll);
		horizontalPanel.add(historyScroll);
		horizontalPanel.setHeight("400px");
		appPanel.add(signOutLink);
		appPanel.add(horizontalPanel);
		appPanel.add(addContactButton);
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

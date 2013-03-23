package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.client.GetContactDialog.GetContactDialogListener;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestService;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestServiceAsync;
import hu.edudroid.blackmarkettmit.client.services.LoginService;
import hu.edudroid.blackmarkettmit.client.services.LoginServiceAsync;
import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.LoginInfo;
import hu.edudroid.blackmarkettmit.shared.PlayerState;
import hu.edudroid.blackmarkettmit.shared.RecommandationRequest;

import java.util.ArrayList;
import java.util.List;

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
	private List<Contact> contactList;
	private LoginInfo loginInfo;
	
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private FlexTable actionTable;
	private VerticalPanel historyPanel;
	private List<RecommandationRequest> recommandationRequests = new ArrayList<RecommandationRequest>(); 
	ContactRequestServiceAsync contactRequestsService = GWT.create(ContactRequestService.class);
	
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
							init();
						} else {
							loadLogin();
						}
					}
				});
	}
	
	private void init() {
		getData();
		initGamePanel();
	}

	private void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		VerticalPanel loginPanel = new VerticalPanel();
		loginPanel.add(new Label("Please log in with you google account to play the game!"));
		loginPanel.add(signInLink);
		RootPanel.get("appPanel").add(loginPanel);
	}

	private void getData() {
		contactRequestsService.getRecommandationRequests(new AsyncCallback<List<RecommandationRequest>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<RecommandationRequest> result) {
				recommandationRequests = result;
				updateHistory();
			}
		});
		contactRequestsService.getContacts(new AsyncCallback<List<Contact>>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(List<Contact> result) {
				contactList = result;
				updateContactTable();
			}
		});
	}
 
	private void initGamePanel() {
		Label userNameLabel = new Label("Hello " + loginInfo.getNickname() + "!");
		Label debugLabel = new Label("Key " + loginInfo.getBlackMarketUser().getUserKey() + " random " + loginInfo.getBlackMarketUser().getRandom());
		signOutLink.setHref(loginInfo.getLogoutUrl());
		
		RootPanel appPanel = RootPanel.get("appPanel");
		actionTable = new FlexTable();
		actionTable.setWidget(0, 0, new Label("Player name"));
		actionTable.setWidget(0, 1, new Label("Games"));
		actionTable.setWidget(0, 2, new Label("Co-op"));
		actionTable.setWidget(0, 3, new Label("Defeat"));
		actionTable.setWidget(0, 4, new Label("I lost"));
		actionTable.setWidget(0, 5, new Label("I won"));
		actionTable.setWidget(0, 6, new Label("Action"));
		historyPanel = new VerticalPanel();
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
		appPanel.add(userNameLabel);
		appPanel.add(debugLabel);
		appPanel.add(signOutLink);
		appPanel.add(horizontalPanel);
		appPanel.add(addContactButton);
	}

	private void updateHistory() {
		historyPanel.clear();
		ArrayList<HistoryItem> history = new ArrayList<HistoryItem>();
		for (RecommandationRequest recommandationRequest : recommandationRequests) {
			history.add(new HistoryItem(recommandationRequest));
		}
		for (HistoryItem item : history) {
			FlowPanel historyElement = new FlowPanel();
			historyElement.add(new Label(item.toString()));
			historyPanel.add(historyElement);
		}
	}

	private void updateContactTable() {
		// Clear table
		while (actionTable.getRowCount() > 1) {
			actionTable.removeRow(1);
		}
		// Order contact list
		if (contactList == null) {
			contactList = new ArrayList<Contact>();
		}
		java.util.Collections.sort(contactList, Contact.getComparator());
		for (Contact player:contactList) {
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
	}

	@Override
	public void getRandom() {
		contactRequestsService.newRandomContact(new AsyncCallback<List<Contact>>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(List<Contact> result) {
				getData();
				getContactDialog.hide();
			}
		});
	}

	@Override
	public void requestContactFromPlayer(Contact player) {
		getContactDialog.hide();
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(addContactButton)) {
			getContactDialog = new GetContactDialog(contactList, this);
		}
	}

	@Override
	public void cooperate(Contact player) {
	}

	@Override
	public void screw(Contact player) {
	}

	@Override
	public void reject(Contact player) {
	}
}

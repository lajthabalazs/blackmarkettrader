package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.client.GetContactDialog.GetContactDialogListener;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestService;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestServiceAsync;
import hu.edudroid.blackmarkettmit.client.services.LoginService;
import hu.edudroid.blackmarkettmit.client.services.LoginServiceAsync;
import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.ContactComparator;
import hu.edudroid.blackmarkettmit.shared.LoginInfo;
import hu.edudroid.blackmarkettmit.shared.PlayerState;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Blackmarkettmit implements EntryPoint, GetContactDialogListener,
		ClickHandler, TradeActionHandler, SuggestListener {
	private GetContactDialog getContactDialog;
	private Button addContactButton;
	private List<Contact> contactList;
	private LoginInfo loginInfo;

	private Anchor signInLink;
	private Anchor signOutLink;
	private FlexTable actionTable;
	private VerticalPanel requestPanel;
	ContactRequestServiceAsync contactRequestsService = GWT
			.create(ContactRequestService.class);
	private SuggestDialog suggestDialog;

	
	public void onModuleLoad() {
		refreshLoginPanel();
		login();
	}

	private void login() {
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {

			public void onFailure(Throwable error) {
			}
	
			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if (loginInfo.isLoggedIn()) {
					init();
				}
				refreshLoginPanel();
			}
		});
	}

	private void init() {
		refreshLoginPanel();
		getData();
		initActionPanel();
		initContactRequestsPanel();
	}

	private void getData() {
		contactRequestsService.getContacts(new AsyncCallback<List<Contact>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<Contact> result) {
				contactList = result;
				updateActionPanel();
				updateRequestsPanel();
			}
		});
	}

	private void refreshLoginPanel(){
		RootPanel loginHolder = RootPanel.get("loginHolder");
		loginHolder.clear();
		if (loginInfo == null || !loginInfo.isLoggedIn()) {
			// Assemble login panel.
			VerticalPanel loginPanel = new VerticalPanel();
			loginPanel.add(new Label("Please log in with you google account to play the game!"));
			if (loginInfo != null) {
				signInLink = new Anchor("Sign In");
				signInLink.setHref(loginInfo.getLoginUrl());
				loginPanel.add(signInLink);
			}
			loginHolder.add(loginPanel);
		} else {
			Label userNameLabel = new Label("Hello " + loginInfo.getNickname() + "!");
			signOutLink = new Anchor("Sign Out");
			signOutLink.setHref(loginInfo.getLogoutUrl());
			loginHolder.add(userNameLabel);
			loginHolder.add(signOutLink);
		}
	}
	
	private void initActionPanel(){
		RootPanel tablePanel = RootPanel.get("actionPanel");
		actionTable = new FlexTable();
		DOM.setElementAttribute(actionTable.getElement(), "id", "actionTable");
		actionTable.setWidget(0, 0, new Label("Player name"));
		actionTable.setWidget(0, 1, new Label("Games"));
		actionTable.setWidget(0, 2, new Label("Co-op"));
		actionTable.setWidget(0, 3, new Label("Defeat"));
		actionTable.setWidget(0, 4, new Label("I lost"));
		actionTable.setWidget(0, 5, new Label("I won"));
		actionTable.setWidget(0, 6, new Label("Action"));
		DOM.setElementAttribute(actionTable.getRowFormatter().getElement(0), "id", "actionTableHeader");
		ScrollPanel actionTableScroll = new ScrollPanel(actionTable);
		actionTableScroll.setHeight("400px");
		tablePanel.add(actionTableScroll);
	}
	
	private void initContactRequestsPanel() {
		RootPanel requestHolder = RootPanel.get("contactRequestPanel");
		addContactButton = new Button("Add new contact");
		addContactButton.addClickHandler(this);
		requestPanel = new VerticalPanel();
		ScrollPanel historyScroll = new ScrollPanel(requestPanel);
		historyScroll.setHeight("400px");
		requestHolder.add(addContactButton);
		requestHolder.add(requestPanel);
	}

	private void updateActionPanel() {
		// Clear table
		while (actionTable.getRowCount() > 1) {
			actionTable.removeRow(1);
		}
		// Order contact list
		if (contactList == null) {
			contactList = new ArrayList<Contact>();
		}
		java.util.Collections.sort(contactList, new ContactComparator());
		for (Contact player : contactList) {
			int nextRow = actionTable.getRowCount();
			if (player.getViewer() == 0) {
				/*actionTable.setWidget(nextRow, 0,
						new Label(player.getSecondDisplayName()));*/
				actionTable.setWidget(nextRow, 0,
						new Label(player.getSecondDebugDisplayName()));
				actionTable.setWidget(nextRow, 1,
						new Label("" + player.getGameCount()));
				actionTable.setWidget(nextRow, 2,
						new Label("" + player.getCooperationCount()));
				actionTable.setWidget(nextRow, 3,
						new Label("" + player.getBothDefectCount()));
				actionTable.setWidget(nextRow, 4,
						new Label("" + player.getSecondDefectCount()));
				actionTable.setWidget(nextRow, 5,
						new Label("" + player.getFirstDefectCount()));
			} else {
				/* actionTable.setWidget(nextRow, 0,
						new Label(player.getFirstDisplayName()));*/
				actionTable.setWidget(nextRow, 0,
						new Label(player.getFirstDebugDisplayName()));
				actionTable.setWidget(nextRow, 1,
						new Label("" + player.getGameCount()));
				actionTable.setWidget(nextRow, 2,
						new Label("" + player.getCooperationCount()));
				actionTable.setWidget(nextRow, 3,
						new Label("" + player.getBothDefectCount()));
				actionTable.setWidget(nextRow, 4,
						new Label("" + player.getFirstDefectCount()));
				actionTable.setWidget(nextRow, 5,
						new Label("" + player.getSecondDefectCount()));
			}
			if (player.getState() == PlayerState.INVITED_HIM) {
				actionTable
						.setWidget(nextRow, 6, new Label("Waiting response"));
			} else if (player.getState() == PlayerState.INVITED_ME) {
				HorizontalPanel buttonHolder = new HorizontalPanel();
				buttonHolder.add(new AcceptButton(player, this));
				buttonHolder.add(new RejectButton(player, this));
				actionTable.setWidget(nextRow, 6, buttonHolder);
			} else {
				actionTable.setWidget(nextRow, 6,
						new InviteButton(player, this));
			}
		}
	}
	
	protected void updateRequestsPanel() {
		requestPanel.clear();
		int count = 0;
		for (Contact contact : contactList) {
			if (contact.getViewer() == 0) {
				if (contact.getSecondPlayerRequestsRecommandation() != null) {
					requestPanel.add(new SuggestPanel(contact, this));
					count ++;
				}
			} else {
				if (contact.getFirstPlayerRequestsRecommandation() != null) {
					requestPanel.add(new SuggestPanel(contact, this));
					count ++;
				}
			}
		}
		if (count == 0) {
			requestPanel.add(new Label("Noone is asking for contacts."));
		}
	}


	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(addContactButton)) {
			getContactDialog = new GetContactDialog(contactList, this);
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
		String playerKey;
		if (player.getViewer() == 0) {
			playerKey = player.getSecondPlayerKey();
		} else {
			playerKey = player.getFirstPlayerKey();
		}
		contactRequestsService.askForRecommandation(playerKey, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void nothing) {
				getData();
				getContactDialog.hide();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
	
	@Override
	public void cooperate(Contact player) {
		play(player, Contact.CHOICE_COOPERATE);
	}

	@Override
	public void screw(Contact player) {
		play(player, Contact.CHOICE_DEFECT);
	}

	@Override
	public void reject(Contact player) {
		play(player, Contact.CHOICE_REJECT);
	}

	private void play(Contact player, int choice) {
		String otherPlayer;
		if (player.getViewer() == 0) {
			otherPlayer = player.getSecondPlayerKey();
		} else {
			otherPlayer = player.getFirstPlayerKey();
		}
		contactRequestsService.play(otherPlayer, choice, new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				delayedUpdate();
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	private void delayedUpdate() {
		Timer t = new Timer() {
			public void run() {
				getData();
			}
		};
		t.schedule(5000);
	}

	@Override
	public void userWantsToSuggestForContact(final Contact contact) {
		String otherPlayer;
		if (contact.getViewer() == 0) {
			otherPlayer = contact.getSecondPlayerKey();
		} else {
			otherPlayer = contact.getFirstPlayerKey();
		}
		contactRequestsService.getAlligibleContacts(otherPlayer, new AsyncCallback<List<Contact>>() {
			
			@Override
			public void onSuccess(List<Contact> result) {
				showSuggestDialog(contact, result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	private void showSuggestDialog(Contact contact, List<Contact> result) {
		suggestDialog = new SuggestDialog(contact, result, this);
	}

	@Override
	public void userSuggestedContactForContact(Contact contact, Contact suggestedContact) {
		String otherPlayer = null;
		String suggestedPlayer = null;
		if (contact.getViewer() == 0) {
			otherPlayer = contact.getSecondPlayerKey();
		} else {
			otherPlayer = contact.getFirstPlayerKey();
		}
		if (suggestedContact.getViewer() == 0) {
			suggestedPlayer = suggestedContact.getSecondPlayerKey();
		} else {
			suggestedPlayer = suggestedContact.getFirstPlayerKey();
		}
		contactRequestsService.suggestContact(otherPlayer, suggestedPlayer, new AsyncCallback<Void>() {			
			@Override
			public void onSuccess(Void result) {
				getData();
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		suggestDialog.hide();
	}
}

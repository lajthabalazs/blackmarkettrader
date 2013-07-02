package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.client.GetContactDialog.GetContactDialogListener;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestService;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestServiceAsync;
import hu.edudroid.blackmarkettmit.client.services.LoginService;
import hu.edudroid.blackmarkettmit.client.services.LoginServiceAsync;
import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.Model;
import hu.edudroid.blackmarkettmit.shared.Notification;
import hu.edudroid.blackmarkettmit.shared.Notification.Component;
import hu.edudroid.blackmarkettmit.shared.TradingEvent;
import hu.edudroid.blackmarkettmit.shared.LoginInfo;
import hu.edudroid.blackmarkettmit.shared.PlayerState;
import hu.edudroid.blackmarkettmit.shared.Tupple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
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
	private static final String VERSION = "17 / Fixed streak bonus notifications";
	private Button addContactButton;
	private LoginInfo loginInfo;
	private Model model = new Model(GWTDayCalculator.DAY_CALCULATOR);
	private long serverTimeAtLastLogin = 0;
	private long clientTimeAtLastLogin = 0;

	private ContactRequestServiceAsync contactRequestsService;
	private LoginServiceAsync loginService;
	private SuggestDialog suggestDialog;
	private VerticalPanel loginPanel;

	public void onModuleLoad() {
		updateLoginPanel();
		final ProgressDialog dialog = new ProgressDialog("Authenticating", "This might take a few seconds. Kick back and relax!");
		// Check login status using login service.
		contactRequestsService = GWT.create(ContactRequestService.class);
		loginService = GWT.create(LoginService.class);
		loginService.checkIfLoggedIn(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {

			public void onFailure(Throwable error) {
				dialog.hide();
				new ErrorDialog("Login error", "While logging in, you received the following error: " + error);
			}
	
			public void onSuccess(LoginInfo result) {
				dialog.hide();
				loginInfo = result;
				serverTimeAtLastLogin = result.getServerTime();
				Date date = new Date();
				clientTimeAtLastLogin = date.getTime();
				model.setLoginInfo(loginInfo);
				if (loginInfo.isLoggedIn()) {
					init();
				} else {
					updateLoginPanel();					
				}
			}
		});
	}
		
	private void init() {
		updateLoginPanel();
		getData();
	}

	private void getData() {
		final ProgressDialog dialog = new ProgressDialog("Updating contacts", "This might take a few seconds. Kick back and relax!");
		contactRequestsService.getContacts(new AsyncCallback<List<Contact>>() {
			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				new ErrorDialog("Something went wrong", "Couldn't retrieve contacts: " + caught);
			}

			@Override
			public void onSuccess(List<Contact> result) {
				dialog.hide();
				updateUI(result);
			}
		});
	}	
	
	private void updateUI(List<Contact> result) {
		RootPanel debugHolder = RootPanel.get("debugHolder");
		Date date = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
		debugHolder.clear();
		debugHolder.add(new Label("Client time " + dtf.format(date)));
		long timeDiff = serverTimeAtLastLogin - clientTimeAtLastLogin;
		Date serverDate = new Date(date.getTime() + timeDiff);
		debugHolder.add(new Label("Server time " + dtf.format(serverDate)));
		debugHolder.add(new Label("Last notification " + dtf.format(new Date(loginInfo.getBlackMarketUser().getLastNotificationView()))));
		// Check login history
		int currentStreak = model.getCurrentStreak();
		RootPanel streakHolder = RootPanel.get("streakHolder");
		streakHolder.clear();
		HorizontalPanel streakPanel = new HorizontalPanel();
		streakPanel.add(new Label("Current streak: "));
		// Add stars
		for (int i = 0; i < 10; i++) {
			if (currentStreak % 10 > i) {
				streakPanel.add(new Label("X"));
			} else {
				streakPanel.add(new Label("O"));
			}
		}
		if (currentStreak > 10) {
			streakPanel.add(new Label((currentStreak / 10) + " in a row."));
		}
		streakHolder.add(streakPanel);		
		model.setContacts(result);
		RootPanel versionPanel = RootPanel.get("versionHolder");
		versionPanel.clear();
		versionPanel.add(new Label("Version " + VERSION));
		updateScoreAndEnergy();
		updateLoginPanel();
		updateActionPanel();
		updateRequestsPanel();
		updateEventsPanel();
		List<Notification> notifications = model.getNotifications();
		if (notifications != null) {
			for (Notification notification : notifications) {
				new NotificationPopup(notification.getTitle(), notification.getMessage(), getWidget(notification.getComponent()));
			}
		}
		// TODO tutorial
		loginService.notificationsDisplayed(true, true, true, new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	private Widget getWidget(Component component) {
		switch (component) {
		case LOGIN_BOX:
			return loginPanel;
		default:
			return null;
		}
	}

	private void updateScoreAndEnergy(){
		RootPanel energyHolder = RootPanel.get("energyHolder");
		energyHolder.clear();
		RootPanel scoreHolder = RootPanel.get("scoreHolder");
		scoreHolder.clear();
		if (loginInfo != null) {
			HorizontalPanel energyPanel = new HorizontalPanel();
			energyPanel.add(new Label("Energy:"));
			energyPanel.add(new Label(model.getRemainingEnergy() + ""));
			energyHolder.add(energyPanel);
			HorizontalPanel scorePanel = new HorizontalPanel();
			scorePanel.add(new Label("Profit: "));
			scorePanel.add(new Label("$"+ model.getTotalScore()));
			energyHolder.add(scorePanel);
		}
	}

	private void updateLoginPanel(){
		RootPanel loginHolder = RootPanel.get("loginHolder");
		loginHolder.clear();
		loginPanel = new VerticalPanel();
		if (loginInfo == null || !loginInfo.isLoggedIn()) {
			// Assemble login panel.
			loginPanel.add(new Label("Log in with Facebook or Google!"));
			if (loginInfo != null) {
				Anchor signInWithFacebookLink = new Anchor("Sign In with Facebook");
				signInWithFacebookLink.setHref(loginInfo.getLoginWithFacebookUrl());
				loginPanel.add(signInWithFacebookLink);
				Anchor signInWithGoogleLink = new Anchor("Sign In with Google");
				signInWithGoogleLink.setHref(loginInfo.getLoginWithGoogleUrl());
				loginPanel.add(signInWithGoogleLink);
			}
		} else {
			Label userNameLabel = new Label("Hello " + loginInfo.getNickname() + "!");
			loginPanel.add(userNameLabel);
			if (loginInfo.getLogoutUrl() != null) {
				Anchor signOutLink = new Anchor("Sign Out");
				signOutLink.setHref(loginInfo.getLogoutUrl());
				loginPanel.add(signOutLink);
			}
		}
		loginHolder.add(loginPanel);
	}
	
	private void updateEventsPanel(){
		RootPanel eventsPanel = RootPanel.get("eventsPanel");
		eventsPanel.clear();
		ArrayList<TradingEvent> allEvents = model.getAllTradingEvents();
		VerticalPanel eventsHolder = new VerticalPanel();
		Collections.sort(allEvents, TradingEvent.EVENT_COMPARATOR);
		long timeDiff = clientTimeAtLastLogin - serverTimeAtLastLogin;
		for (TradingEvent event : allEvents) {
			eventsHolder.add(new Label(event.getString(timeDiff, GWTDayCalculator.DAY_CALCULATOR)));
		}
		ScrollPanel eventsScroll = new ScrollPanel(eventsHolder);
		eventsScroll.setHeight("400px");
		eventsPanel.add(eventsScroll);
	}

	private void updateActionPanel() {
		RootPanel tablePanel = RootPanel.get("actionPanel");
		tablePanel.clear();
		FlexTable actionTable = new FlexTable();
		DOM.setElementAttribute(actionTable.getElement(), "id", "actionTable");
		actionTable.setWidget(0, 0, new Label("Contact"));
		actionTable.setWidget(0, 1, new Label("Games"));
		actionTable.setWidget(0, 2, new Label("Total profit"));
		actionTable.setWidget(0, 3, new Label("Action"));
		DOM.setElementAttribute(actionTable.getRowFormatter().getElement(0), "id", "actionTableHeader");
		ScrollPanel actionTableScroll = new ScrollPanel(actionTable);
		actionTableScroll.setHeight("400px");
		tablePanel.add(actionTableScroll);
		// Order contact list
		List<Contact>contactList = model.getOrderedContacts();
		for (Contact player : contactList) {
			List<TradingEvent> events = player.getEvents();
			int totalScore = 0;
			for (TradingEvent event : events){
				totalScore += event.getPointValue();
			}
			int nextRow = actionTable.getRowCount();
			if(player.getViewer() == 1) {
				actionTable.setWidget(nextRow, 0,
						new Label(player.getFirstDisplayName()));
			} else {
				actionTable.setWidget(nextRow, 0,
						new Label(player.getSecondDisplayName()));
			}
			actionTable.setWidget(nextRow, 1,
					new Label("" + player.getGameCount()));
			actionTable.setWidget(nextRow, 2,
					new Label("$" + totalScore));
			if (player.getState() == PlayerState.INVITED_HIM) {
				actionTable
						.setWidget(nextRow, 3, new Label("Waiting response"));
			} else if (player.getState() == PlayerState.INVITED_ME) {
				HorizontalPanel buttonHolder = new HorizontalPanel();
				AcceptButton acceptButton = new AcceptButton(player, this);
				buttonHolder.add(acceptButton);
				acceptButton.setEnabled(model.getRemainingEnergy() >= Contact.ENERGY_CONSUMPTION_ACCEPT);
				RejectButton rejectButton = new RejectButton(player, this);
				buttonHolder.add(rejectButton);
				rejectButton.setEnabled(model.getRemainingEnergy() >= Contact.ENERGY_CONSUMPTION_REJECT);
				actionTable.setWidget(nextRow, 3, buttonHolder);
			} else {
				InviteButton inviteButton = new InviteButton(player, this);
				inviteButton.setEnabled(model.getRemainingEnergy() >= Contact.ENERGY_CONSUMPTION_INVITE);
				actionTable.setWidget(nextRow, 3, inviteButton);
			}
		}
	}
	
	protected void updateRequestsPanel() {
		RootPanel requestHolder = RootPanel.get("contactRequestPanel");
		requestHolder.clear();
		VerticalPanel requestPanel = new VerticalPanel();
		int count = 0;
		List<Contact>contactList = model.getOrderedContacts();		
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
			requestPanel.add(new Label("No one is asking for contacts."));
		}
		addContactButton = new Button("Add new contact");
		addContactButton.addClickHandler(this);
		ScrollPanel historyScroll = new ScrollPanel(requestPanel);
		historyScroll.setHeight("400px");
		requestHolder.add(addContactButton);
		requestHolder.add(requestPanel);
	}


	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(addContactButton)) {
			List<Contact>contactList = model.getOrderedContacts();
			getContactDialog = new GetContactDialog(contactList, model.getRemainingEnergy(), this);
		}
	}

	@Override
	public void getRandomContact() {		
		final ProgressDialog dialog = new ProgressDialog("Finding a random contact", "This might take a few seconds. Kick back and relax!");		
		contactRequestsService.newRandomContact(new AsyncCallback<Tupple<Contact,List<Contact>>>() {
			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				new ErrorDialog("Something went wrong", "Couldn't get a random contact: " + caught);
			}

			@Override
			public void onSuccess(Tupple<Contact,List<Contact>> result) {
				dialog.hide();
				getContactDialog.hide();
				if (result.getFirst() == null) {
					new ErrorDialog("No new contacts found", "Sorry mate, it seems you're out of luck. We couldn't find any new contacts for you.");
				} else {
					String contactName = "";
					if (result.getFirst().getViewer() == 0) {
						contactName = result.getFirst().getSecondDisplayName();
					} else {
						contactName = result.getFirst().getFirstDisplayName();
					}
					// Check if this was the first contact
					List<Contact>contactList = model.getOrderedContacts();
					if (contactList.size() == 0) {
						new TutorialPopup("Your first contact", "Congrats, you just made your first contact. It's time to get trading. Trade the original, trade the fake, it's your choice. There are no consequences, it's only up to you two.", null);
					} else {
						new MessageDialog("Meet " + contactName + "!", "This is the beginning of a beautiful friendship...");
					}
				}
				updateUI(result.getSecond());
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
		final ProgressDialog dialog = new ProgressDialog("Asking for recommandations", "This might take a few seconds. Kick back and relax!");
		contactRequestsService.askForRecommandation(playerKey, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void nothing) {
				dialog.hide();
				getData();
				getContactDialog.hide();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				new ErrorDialog("Something went wrong", "Couldn't get contact from player: " + caught);				
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
		String name;
		if (player.getViewer() == 0) {
			otherPlayer = player.getSecondPlayerKey();
			name = player.getSecondDisplayName();
		} else {
			otherPlayer = player.getFirstPlayerKey();
			name = player.getFirstDisplayName();
		}
		final String otherPlayerName = name;
		final ProgressDialog dialog = new ProgressDialog("Processing action", "This might take a few seconds. Kick back and relax!");
		contactRequestsService.play(otherPlayer, choice, new AsyncCallback<Tupple<Integer,List<Contact>>>() {
			@Override
			public void onSuccess(Tupple<Integer,List<Contact>> result) {
				if (result.getSecond() != null) {
					model.setContacts(result.getSecond());
				} else {
					new ErrorDialog("No contact update received", "Try reloading the page!");
				}
				dialog.hide();
				if (result.getFirst() == ContactRequestService.PLAY_RESULT_COOPERATE) {
					new MessageDialog("You got your money", "Congrats! You were right to trust " + otherPlayerName + ". You both got what you wanted. Cheers to an honest deal!");
					updateUI(result.getSecond());
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_BOTH_DEFECTED) {
					new MessageDialog("The money was fake", "Well that was a waste of time. Thank god you didn't fall for " + otherPlayerName + "'s trick.");
					updateUI(result.getSecond());
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_I_DEFECTED) {
					new MessageDialog(otherPlayerName + " paid the price", otherPlayerName + ", what a looser. Taking candy from a baby...");
					updateUI(result.getSecond());
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_HE_DEFECTED) {
					new MessageDialog("The money was fake", "Backstabbing bastard! You were wrong to trust " + otherPlayerName + ".");
					updateUI(result.getSecond());
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_ACCEPTED) {
					updateUI(result.getSecond());
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_DECLINED) {
					new MessageDialog("Rejected", "Somthing went wrong, please wait while updating contact list.");
					updateUI(result.getSecond());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				new ErrorDialog("Something went wrong", "" + caught);
			}
		});
	}
	
	@Override
	public void userWantsToSuggestForContact(final Contact contact) {
		String otherPlayer;
		final ProgressDialog dialog = new ProgressDialog("Constructing list of possible suggestions", "This might take a few seconds. Kick back and relax!");
		if (contact.getViewer() == 0) {
			otherPlayer = contact.getSecondPlayerKey();
		} else {
			otherPlayer = contact.getFirstPlayerKey();
		}
		contactRequestsService.getAlligibleContacts(otherPlayer, new AsyncCallback<List<Contact>>() {
			
			@Override
			public void onSuccess(List<Contact> result) {
				dialog.hide();
				showSuggestDialog(contact, result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				new ErrorDialog("Something went wrong", "" + caught);
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
		final ProgressDialog dialog = new ProgressDialog("Processing suggestion", "This might take a few seconds. Kick back and relax!");
		contactRequestsService.suggestContact(otherPlayer, suggestedPlayer, new AsyncCallback<Void>() {			
			@Override
			public void onSuccess(Void result) {
				dialog.hide();
				getData();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				new ErrorDialog("Something went wrong", "" + caught);
			}
		});
		suggestDialog.hide();
	}
}
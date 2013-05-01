package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.client.GetContactDialog.GetContactDialogListener;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestService;
import hu.edudroid.blackmarkettmit.client.services.ContactRequestServiceAsync;
import hu.edudroid.blackmarkettmit.client.services.LoginService;
import hu.edudroid.blackmarkettmit.client.services.LoginServiceAsync;
import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.ContactComparator;
import hu.edudroid.blackmarkettmit.shared.Date;
import hu.edudroid.blackmarkettmit.shared.LoginBasedRewardsAndBadges;
import hu.edudroid.blackmarkettmit.shared.TradingEvent;
import hu.edudroid.blackmarkettmit.shared.LoginInfo;
import hu.edudroid.blackmarkettmit.shared.PlayerState;
import hu.edudroid.blackmarkettmit.shared.Tupple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Blackmarkettmit implements EntryPoint, GetContactDialogListener,
		ClickHandler, TradeActionHandler, SuggestListener {
	private GetContactDialog getContactDialog;
	private static final String VERSION = "11 / gangsta' names";
	private Button addContactButton;
	private List<Contact> contactList;
	private LoginInfo loginInfo;
	private int remainingEnergy = 0;
	private int totalScore = 0;

	ContactRequestServiceAsync contactRequestsService = GWT.create(ContactRequestService.class);
	private SuggestDialog suggestDialog;

	public void onModuleLoad() {
		updateLoginPanel();
		checkIfLoggedIn();
	}
	
	public void checkIfLoggedIn(){
		final ProgressDialog dialog = new ProgressDialog("Authenticating", "This might take a few seconds. Kick back and relax!");
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.checkIfLoggedIn(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {

			public void onFailure(Throwable error) {
				dialog.hide();
				new ErrorDialog("Login error", "While logging in, you received the following error: " + error);
			}
	
			public void onSuccess(LoginInfo result) {
				dialog.hide();
				loginInfo = result;
				if (loginInfo.isLoggedIn()) {
					init();
				}
				updateLoginPanel();
			}
		});
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
	
	private void processContacts(List<Contact> contacts) {
		Date currentDate = GWTDayCalculator.DAY_CALCULATOR.fromDate(new java.util.Date());
		this.contactList = contacts;
		if (loginInfo != null) {
			int usedEnergy = 0;
			for (Contact contact : contactList) {
				// Check if contact creation reduced energy
				if (contact.getViewer() == contact.getWhoRequested()) {
					Date requestDate = GWTDayCalculator.DAY_CALCULATOR.fromDate(contact.getRequestDate());
					if (requestDate.equals(currentDate)) {
						usedEnergy += Contact.ENERGY_CONSUMPTION_CONTACT_REQUEST;
					}
				}
				// Check if contact request reduced energy
				if (contact.getViewer() == 0) {
					if (contact.getFirstPlayerRequestsRecommandation() != null &&
							 GWTDayCalculator.DAY_CALCULATOR.fromDate(contact.getFirstPlayerRequestsRecommandation()).equals(currentDate)) {
						usedEnergy += Contact.ENERGY_CONSUMPTION_CONTACT_REQUEST;
					}
				} else {
					if (contact.getSecondPlayerRequestsRecommandation() != null &&
							 GWTDayCalculator.DAY_CALCULATOR.fromDate(contact.getSecondPlayerRequestsRecommandation()).equals(currentDate)) {
						usedEnergy += Contact.ENERGY_CONSUMPTION_CONTACT_REQUEST;
					}
				}
				List<TradingEvent> events = contact.getEvents();
				for (TradingEvent event : events) {
					totalScore += event.getPointValue();
					if (event.getDate().equals(currentDate)) {
						usedEnergy += event.getUsedEnergy();
					}
				}
			}
			remainingEnergy = loginInfo.getBlackMarketUser().getMaxEnergy() - usedEnergy;
		}
	}
	
	private void init() {
		updateLoginPanel();
		getData();
	}

	private void updateUI(List<Contact> result) {
		totalScore = 0;
		// Check login history
		LoginBasedRewardsAndBadges rewards = new LoginBasedRewardsAndBadges(loginInfo.getBlackMarketUser().getLoginDates(), GWTDayCalculator.DAY_CALCULATOR);
		int currentStreak = rewards.getCurrentStreak();
		int longestStreak = rewards.getLongestStreak();
		int missedDays = rewards.getMissedDays();
		totalScore = totalScore + rewards.getTotalBonus();
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
		processContacts(result);
		RootPanel versionPanel = RootPanel.get("versionHolder");
		versionPanel.clear();
		versionPanel.add(new Label("Version " + VERSION));
		updateScoreAndEnergy();
		updateLoginPanel();
		updateActionPanel();
		updateRequestsPanel();
		updateEventsPanel();
	}

	private void updateScoreAndEnergy(){
		RootPanel energyHolder = RootPanel.get("energyHolder");
		energyHolder.clear();
		RootPanel scoreHolder = RootPanel.get("scoreHolder");
		scoreHolder.clear();
		if (loginInfo != null) {
			HorizontalPanel energyPanel = new HorizontalPanel();
			energyPanel.add(new Label("Energy:"));
			energyPanel.add(new Label(remainingEnergy + ""));
			energyHolder.add(energyPanel);
			HorizontalPanel scorePanel = new HorizontalPanel();
			scorePanel.add(new Label("Profit: "));
			scorePanel.add(new Label("$"+ totalScore));
			energyHolder.add(scorePanel);
		}
	}

	private void updateLoginPanel(){
		RootPanel loginHolder = RootPanel.get("loginHolder");
		loginHolder.clear();
		VerticalPanel loginPanel = new VerticalPanel();
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
		ArrayList<TradingEvent> allEvents = new ArrayList<TradingEvent>();
		for (Contact contact : contactList) {
			allEvents.addAll(contact.getEvents());
		}
		VerticalPanel eventsHolder = new VerticalPanel();
		Collections.sort(allEvents, TradingEvent.EVENT_COMPARATOR);
		for (TradingEvent event : allEvents) {
			eventsHolder.add(new Label(event.toString()));
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
		if (contactList == null) {
			contactList = new ArrayList<Contact>();
		}
		java.util.Collections.sort(contactList, new ContactComparator());
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
				acceptButton.setEnabled(remainingEnergy >= Contact.ENERGY_CONSUMPTION_ACCEPT);
				RejectButton rejectButton = new RejectButton(player, this);
				buttonHolder.add(rejectButton);
				rejectButton.setEnabled(remainingEnergy >= Contact.ENERGY_CONSUMPTION_REJECT);
				actionTable.setWidget(nextRow, 3, buttonHolder);
			} else {
				InviteButton inviteButton = new InviteButton(player, this);
				inviteButton.setEnabled(remainingEnergy >= Contact.ENERGY_CONSUMPTION_INVITE);
				actionTable.setWidget(nextRow, 3, inviteButton);
			}
		}
	}
	
	protected void updateRequestsPanel() {
		RootPanel requestHolder = RootPanel.get("contactRequestPanel");
		requestHolder.clear();
		VerticalPanel requestPanel = new VerticalPanel();
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
			getContactDialog = new GetContactDialog(contactList, remainingEnergy, this);
		}
	}

	@Override
	public void getRandom() {
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
					new MessageDialog("Meet " + contactName + "!", "This is the beginning of a beautiful friendship..");
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
					contactList = result.getSecond();
				} else {
					new ErrorDialog("No new contacts", "We didn't receive any new contacts.");
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
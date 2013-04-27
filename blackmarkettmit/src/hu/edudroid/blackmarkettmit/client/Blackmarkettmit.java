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
import hu.edudroid.blackmarkettmit.shared.Tupple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private static final String VERSION = "1";
	private Button addContactButton;
	private List<Contact> contactList;
	private LoginInfo loginInfo;

	private FlexTable actionTable;
	private VerticalPanel requestPanel;
	ContactRequestServiceAsync contactRequestsService = GWT.create(ContactRequestService.class);
	private SuggestDialog suggestDialog;

	
	public void onModuleLoad() {
		refreshLoginPanel();
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
				refreshLoginPanel();
			}
		});
	}

	private void init() {
		refreshLoginPanel();
		initActionPanel();
		initContactRequestsPanel();
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
				contactList = result;
				updateUI();
			}
		});
	}
	
	private void updateUI() {
		updateActionPanel();
		updateRequestsPanel();
		updateEventsPanel();
	}

	private void refreshLoginPanel(){
		RootPanel loginHolder = RootPanel.get("loginHolder");
		loginHolder.clear();
		VerticalPanel loginPanel = new VerticalPanel();
		loginPanel.add(new Label("Version " + VERSION));
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
	
	private void initActionPanel(){
		RootPanel tablePanel = RootPanel.get("actionPanel");
		actionTable = new FlexTable();
		DOM.setElementAttribute(actionTable.getElement(), "id", "actionTable");
		actionTable.setWidget(0, 0, new Label("Player name"));
		actionTable.setWidget(0, 1, new Label("Games"));
		actionTable.setWidget(0, 2, new Label("Total profit"));
		actionTable.setWidget(0, 3, new Label("Action"));
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

	private void updateEventsPanel(){
		RootPanel eventsPanel = RootPanel.get("eventsPanel");
		eventsPanel.clear();
		ArrayList<Event> events = new ArrayList<Event>();
		for (Contact contact : contactList) {
			byte[] tradeHistory = contact.getTradeHistory();
			int eventCount = tradeHistory.length / (Contact.TRADE_HISTORY_ENTRY_LENGTH * 2);
			if (tradeHistory.length != eventCount * 2 * Contact.TRADE_HISTORY_ENTRY_LENGTH) {
				eventCount ++;
			}
			for (int i = 0; i < eventCount; i++) {
				events.add(new Event(contact, i));
			}
		}
		VerticalPanel eventsHolder = new VerticalPanel();
		Collections.sort(events, new Comparator<Event>() {

			@Override
			public int compare(Event e1, Event e2) {
				return e1.dateString.compareTo(e2.dateString);
			}
		});
		for (Event event : events) {
			eventsHolder.add(new Label(event.toString()));
		}
		ScrollPanel eventsScroll = new ScrollPanel(eventsHolder);
		eventsScroll.setHeight("400px");
		eventsPanel.add(eventsScroll);
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
			actionTable.setWidget(nextRow, 0,
					new Label(player.getSecondDisplayName()));
			actionTable.setWidget(nextRow, 1,
					new Label("G: " + player.getGameCount() + " TH: " + player.getTradeHistory().length));
			actionTable.setWidget(nextRow, 2,
					new Label(player.getSecondDisplayName()));
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
			requestPanel.add(new Label("No one is asking for contacts."));
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
				contactList = result.getSecond();
				updateUI();
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
					updateUI();
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_BOTH_DEFECTED) {
					new MessageDialog("The money was fake", "Well that was a waste of time. Thank god you didn't fall for " + otherPlayerName + "'s trick.");
					updateUI();
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_I_DEFECTED) {
					new MessageDialog(otherPlayerName + " paid the price", otherPlayerName + ", what a looser. Taking candy from a baby...");
					updateUI();
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_HE_DEFECTED) {
					new MessageDialog("The money was fake", "Backstabbing bastard! You were wrong to trust " + otherPlayerName + ".");
					updateUI();
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_ACCEPTED) {
					updateUI();
				} else if (result.getFirst() == ContactRequestService.PLAY_RESULT_DECLINED) {
					new MessageDialog("Rejected", "Somthing went wrong, please wait while updating contact list.");
					updateUI();
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
	
	private class Event {
		Contact contact;
		int player;
		byte event;
		byte previousEvent;
		String dateString;

		public Event(Contact contact, int index) {
			byte[] tradeHistory = contact.getTradeHistory();
			this.contact = contact;
			int startPosition = index * Contact.TRADE_HISTORY_ENTRY_LENGTH * 2;
			if (startPosition + Contact.TRADE_HISTORY_ENTRY_LENGTH * 2 <= tradeHistory.length) {
				previousEvent = tradeHistory[startPosition + 7];
				startPosition = startPosition + Contact.TRADE_HISTORY_ENTRY_LENGTH;
			} else {
				previousEvent = Contact.HISTORY_INVALID;
			}
			dateString = "" + (tradeHistory[startPosition] + Contact.START_YEAR);
			dateString = dateString + "-" + tradeHistory[startPosition + 1];
			dateString = dateString + "-" + tradeHistory[startPosition + 2];
			dateString = dateString + " " + tradeHistory[startPosition + 3];
			dateString = dateString + ":" + tradeHistory[startPosition + 4];
			dateString = dateString + ":" + tradeHistory[startPosition + 5];
			this.player = tradeHistory[startPosition + 6];
			this.event = tradeHistory[startPosition + 7];
		}
		
		@Override
		public String toString() {
			String base =  byteArrayToDisplayString(contact.getTradeHistory());
			String playerName = contact.getSecondDisplayName();
			if (contact.getViewer() != 0) {
				playerName = contact.getFirstDisplayName();
			}
			if (player != contact.getViewer()) {
				base = base + " not my event ";
				if ((event == Contact.HISTORY_INVITE_AND_COOP)||(event == Contact.HISTORY_INVITE_AND_DEFECT)) {
					return base + " " + dateString + " - " + playerName + " invited you to trade.";
				} else if (event == Contact.HISTORY_REJECT) {
					return base + " " +  dateString + " - " + playerName + " rejected your invitation.";
				} else {
					base = base + " response ";
					if (event == Contact.HISTORY_ACCEPT_AND_COOP) {
						if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
							return base + " " +  dateString + " - " + playerName + " and you both cooperated.";	
						} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
							return base + " " +  dateString + " - You defected on " + playerName + ".";
						}
					} else if (event == Contact.HISTORY_ACCEPT_AND_DEFECT) {
						if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
							return base + " " +  dateString + " - " + playerName + " defected on you.";	
						} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
							return base + " " +  dateString + " - " + playerName + " and you both defected.";
						}
					}
				}
			} else {
				base = base + " my event ";
				if ((event == Contact.HISTORY_INVITE_AND_COOP)||(event == Contact.HISTORY_INVITE_AND_DEFECT)) {
					return base + " " +  dateString + " - You invited " + playerName + " to trade.";
				} else if (event == Contact.HISTORY_REJECT) {
					return base + " " +  dateString + " - You rejected " + playerName + "'s invitation to trade.";
				} else {
					base = base + " response ";
					if (event == Contact.HISTORY_ACCEPT_AND_COOP) {
						if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
							return base + " " +  dateString + " - " + playerName + " and you both cooperated.";	
						} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
							return base + " " +  dateString + " - " + playerName + " defected on you.";	
						}
					} else if (event == Contact.HISTORY_ACCEPT_AND_DEFECT) {
						if (previousEvent == Contact.HISTORY_INVITE_AND_COOP) {
							return base + " " +  dateString + " - You defected on " + playerName + ".";							
						} else if (previousEvent == Contact.HISTORY_INVITE_AND_DEFECT) {
							return base + " " +  dateString + " - " + playerName + " and you both defected.";
						}
					}
				}
			}
			return base + " " + dateString + " - " + playerName + ", unknown event.";
		}
	}
	
	private static String byteArrayToDisplayString(byte[] array) {
		String ret = "";
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				if (i % 8 == 0) {
					ret = ret + " / ";
				} else {
					ret = ret +", ";
				}
			}
			ret = ret + array[i];
		}
		return ret;
	}
}
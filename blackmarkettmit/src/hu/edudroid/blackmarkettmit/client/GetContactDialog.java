package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Player;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GetContactDialog extends DialogBox implements ClickHandler {

	ArrayList<Button> requestContactButtons = new ArrayList<Button>();
	Button cancelButton;
	Button randomButton;
	private GetContactDialogListener listener;
	private ArrayList<Player> players;
	
	public GetContactDialog(ArrayList<Player> players, GetContactDialogListener listener) {
		this.listener = listener;
		this.players = players;
		setText("Get a new contact");
		VerticalPanel mainPanel = new VerticalPanel();
		FlexTable table = new FlexTable();
		for (int i = 0; i < players.size(); i++) {
			table.setWidget(i, 0, new Label(players.get(i).getDisplayName()));
			Button requestContactButton = new Button("Request contact");
			requestContactButtons.add(requestContactButton);
			requestContactButton.addClickHandler(this);
			table.setWidget(i, 1, requestContactButton);
			table.getCellFormatter().setAlignment(i, 1,  HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
		}
		cancelButton = new Button("Never mind");
		cancelButton.addClickHandler(this);		
		randomButton = new Button("Find me someone (Costs $10)");
		randomButton.addClickHandler(this);
		table.setWidth("100%");
		ScrollPanel tableScrollPanel = new ScrollPanel(table);
		tableScrollPanel.setSize("300px","200px");
		mainPanel.add(tableScrollPanel);
		mainPanel.add(randomButton);
		mainPanel.add(cancelButton);
		setWidget(mainPanel);
		show();
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(randomButton)) {
			listener.getRandom();
		} else {
			int index = requestContactButtons.indexOf(event.getSource());
			if (index != -1) {
				listener.requestContactFromPlayer(players.get(index));
			}
		}
		this.hide();
	}

	public interface GetContactDialogListener {
		void getRandom();
		void requestContactFromPlayer(Player player);
	}
}
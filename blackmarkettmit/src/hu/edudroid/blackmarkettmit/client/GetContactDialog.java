package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GetContactDialog extends DialogBox implements ClickHandler {

	Button cancelButton;
	Button randomButton;
	private GetContactDialogListener listener;
	
	public GetContactDialog(List<Contact> players, GetContactDialogListener listener) {
		this.listener = listener;
		setText("Get a new contact");
		VerticalPanel mainPanel = new VerticalPanel();
		FlexTable table = new FlexTable();
		if (players!=null) {
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getViewer() == 0) {
					table.setWidget(i, 0, new Label(players.get(i).getSecondDebugDisplayName()));
					if (players.get(i).getFirstPlayerRequestsRecommandation() == null) {
						Button requestContactButton = new RequestContactButton(players.get(i), listener);
						table.setWidget(i, 1, requestContactButton);
					} else {
						table.setWidget(i, 1, new Label("Already requested"));
					}
				} else {
					table.setWidget(i, 0, new Label(players.get(i).getFirstDebugDisplayName()));
					if (players.get(i).getSecondPlayerRequestsRecommandation() == null) {
						Button requestContactButton = new RequestContactButton(players.get(i), listener);
						table.setWidget(i, 1, requestContactButton);
					} else {
						table.setWidget(i, 1, new Label("Already requested"));
					}
				}
				table.getCellFormatter().setAlignment(i, 1,  HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
			}
		}
		cancelButton = new Button("Never mind");
		cancelButton.addClickHandler(this);		
		randomButton = new Button("Find me someone (Costs $10)");
		randomButton.addClickHandler(this);
		table.setWidth("100%");
		ScrollPanel tableScrollPanel = new ScrollPanel(table);
		tableScrollPanel.setHeight("200px");
		mainPanel.add(tableScrollPanel);
		mainPanel.add(randomButton);
		mainPanel.add(cancelButton);
		setWidget(mainPanel);
		center();
		show();
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(randomButton)) {
			listener.getRandom();
		} else {
			this.hide();
		}
	}

	public interface GetContactDialogListener {
		void getRandom();
		void requestContactFromPlayer(Contact player);
	}
}
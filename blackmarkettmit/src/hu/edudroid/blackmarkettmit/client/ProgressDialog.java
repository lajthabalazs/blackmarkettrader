package hu.edudroid.blackmarkettmit.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProgressDialog extends DialogBox implements ClickHandler{
	public ProgressDialog(String title, String message) {
		VerticalPanel mainPanel = new VerticalPanel();
		setText(title);
		Label label = new Label(message);
		label.setWidth("200px");
		Button cancelButton = new Button("Do your thing in the background");
		cancelButton.addClickHandler(this);
		mainPanel.add(label);
		mainPanel.add(cancelButton);
		setWidget(mainPanel);
		show();		
	}

	@Override
	public void onClick(ClickEvent event) {
		this.hide();
	}
}

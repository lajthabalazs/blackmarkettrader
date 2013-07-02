package hu.edudroid.blackmarkettmit.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NotificationPopup extends DialogBox implements ClickHandler{

	public NotificationPopup(String title, String message, Widget parent) {
		VerticalPanel mainPanel = new VerticalPanel();
		setText(title);
		Label label = new Label(message);
		label.setWidth("200px");
		Button cancelButton = new Button("Got it!");
		cancelButton.addClickHandler(this);
		mainPanel.add(label);
		mainPanel.add(cancelButton);
		setWidget(mainPanel);
		if (parent != null) {
			setPopupPosition(parent.getAbsoluteLeft(), parent.getAbsoluteTop());
		} else {
			center();
		}
		show();		
	}
	
	@Override
	protected void beginDragging(MouseDownEvent event) {
		event.preventDefault();
	}

	@Override
	public void onClick(ClickEvent event) {
		hide();
	}
}

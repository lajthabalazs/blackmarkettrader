package hu.edudroid.blackmarkettmit.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginDialog extends DialogBox implements ClickHandler{
	
	private TextBox userName;
	private TextBox passWord;
	private LoginListener listener;

	public LoginDialog(LoginListener listener) {
		this.listener = listener;
		VerticalPanel mainPanel = new VerticalPanel();
		// Set the dialog box's caption.
		setText("Welcome!");
		Label label = new Label("Hi there! Let's play a game, but first you have to log in!");
		userName = new TextBox();
		passWord = new TextBox();
		Button loginButton = new Button("Log in!");
		loginButton.addClickHandler(this);
		
		mainPanel.add(label);
		mainPanel.add(userName);
		mainPanel.add(passWord);
		mainPanel.add(loginButton);
		setWidget(mainPanel);
		show();
	}

	@Override
	public void onClick(ClickEvent event) {
		listener.login(userName.getText(), passWord.getText());
	}

	public interface LoginListener {
		void login(String userName, String password);
	}
}

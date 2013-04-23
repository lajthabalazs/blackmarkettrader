package hu.edudroid.blackmarkettmit.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.face4j.facebook.Client;
import com.face4j.facebook.enums.Display;
import com.face4j.facebook.enums.HttpClientType;
import com.face4j.facebook.enums.Permission;
import com.face4j.facebook.factory.FacebookFactory;

public class FacebookServlet extends HttpServlet {

	static final String APP_SECRET = "d6c54e72f86bea02c911e8dd1202daae";
	static final String APP_ID = "233194900159695";
	static final String REDIRECT_URL = "http://blackmarkettmit.appspot.com/FacebookLoginServlet";
	
	static final String USER_ID = "user_id";
	static final String USER_NAME = "user_name";

	static FacebookFactory facebookFactory;

	private static final long serialVersionUID = -5750323052726353067L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FacebookFactory facebookFactory = getFacebookFactory();
		String redirectURL = facebookFactory.getRedirectURL(REDIRECT_URL, Display.POPUP, Permission.EMAIL, Permission.OFFLINE_ACCESS, Permission.PUBLISH_STREAM, Permission.USER_EDUCATION_HISTORY, Permission.USER_RELATIONSHIP_DETAILS, Permission.READ_FRIENDLISTS);
		System.out.println("Redirecting " + redirectURL);
		resp.sendRedirect(redirectURL);
	}

	public static FacebookFactory getFacebookFactory() {
		if (facebookFactory == null) {
			Client client = new Client(APP_ID, APP_SECRET);
			facebookFactory = new FacebookFactory(client, HttpClientType.URL_FETCH_SERVICE);
		}
		return facebookFactory;
	}
}
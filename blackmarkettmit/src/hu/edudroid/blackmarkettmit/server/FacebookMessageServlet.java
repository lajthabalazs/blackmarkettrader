package hu.edudroid.blackmarkettmit.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.face4j.facebook.Facebook;
import com.face4j.facebook.OAuthAccessToken;
//import com.face4j.facebook.enums.Display;
//import com.face4j.facebook.enums.Permission;
import com.face4j.facebook.exception.FacebookException;
import com.face4j.facebook.factory.FacebookFactory;

public class FacebookMessageServlet  extends HttpServlet {
	
	private static final long serialVersionUID = 4196586435464831655L;
	private static final String FACEBOOK_MESSAGE_KEY = "FacebookMessage";

	public static void postToFacebook(String message, HttpSession session) {
		String code = (String)session.getAttribute("code");
		session.setAttribute(FACEBOOK_MESSAGE_KEY, message);
		FacebookFactory facebookFactory = FacebookServlet.getFacebookFactory();
		try {
			OAuthAccessToken accessToken = facebookFactory.getOAuthAccessToken(code, "http://blackmarkettmit.appspot.com/FacebookMessageServlet");
			Facebook facebook = facebookFactory.getInstance(accessToken);
			facebook.wallPost(message, null, "http://blackmarkettmit.appspot.com", null, null, null, null, null);
			session.removeAttribute(FACEBOOK_MESSAGE_KEY);
		} catch (FacebookException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String code = (String)session.getAttribute("code");
		String message = (String)session.getAttribute(FACEBOOK_MESSAGE_KEY);
		FacebookFactory facebookFactory = FacebookServlet.getFacebookFactory();
		try {
			OAuthAccessToken accessToken = facebookFactory.getOAuthAccessToken(code, "http://blackmarkettmit.appspot.com/FacebookMessageServlet");
			Facebook facebook = facebookFactory.getInstance(accessToken);
			facebook.wallPost(message, null, "http://blackmarkettmit.appspot.com", null, null, null, null, null);
			session.removeAttribute(FACEBOOK_MESSAGE_KEY);
		} catch (FacebookException e) {
			e.printStackTrace();
		}
	}
}
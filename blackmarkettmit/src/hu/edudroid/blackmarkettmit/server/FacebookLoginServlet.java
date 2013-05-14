package hu.edudroid.blackmarkettmit.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.face4j.facebook.Facebook;
import com.face4j.facebook.OAuthAccessToken;
import com.face4j.facebook.entity.User;
import com.face4j.facebook.exception.FacebookException;
import com.face4j.facebook.factory.FacebookFactory;

public class FacebookLoginServlet extends HttpServlet {

	private static final long serialVersionUID = -5750323052726353067L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String code = req.getParameter("code");
		FacebookFactory facebookFactory = FacebookServlet.getFacebookFactory();
		try {
			OAuthAccessToken accessToken = facebookFactory.getOAuthAccessToken(code, "http://blackmarkettmit.appspot.com/FacebookLoginServlet");
			Facebook facebook = facebookFactory.getInstance(accessToken);
			User fbUser = facebook.getCurrentUser();			
			System.out.println("User name " + fbUser.getName());
			System.out.println("User id " + fbUser.getId());
			HttpSession session = req.getSession();
			session.setAttribute(FacebookServlet.USER_ID, fbUser.getId());
			session.setAttribute(FacebookServlet.USER_NAME, fbUser.getName());
			session.setAttribute("emailAddress", fbUser.getEmail());
			String gender = fbUser.getGender();
			char genderChar = 'u';
			if (gender != null) {
				if (gender.equals("male")) {
					genderChar = 'm';
				} else if (gender.equals("female")) {
					genderChar = 'f';
				}
			}
			session.setAttribute("gender", genderChar);
			System.out.println("Code " + code);
			resp.sendRedirect("/");
		} catch (FacebookException e) {
			resp.getOutputStream().print("Error logging in with Facebook " + e);
			e.printStackTrace();
		}
	}
}
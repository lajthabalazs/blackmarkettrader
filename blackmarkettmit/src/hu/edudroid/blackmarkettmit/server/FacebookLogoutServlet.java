package hu.edudroid.blackmarkettmit.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FacebookLogoutServlet extends HttpServlet {

	private static final long serialVersionUID = -5750323052726353067L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		session.removeAttribute(FacebookServlet.USER_ID);
		resp.sendRedirect("/");
	}
}
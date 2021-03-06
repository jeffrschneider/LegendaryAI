package ai.legendary.infrastructure;

import java.io.IOException;
import java.util.Scanner;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*@WebServlet(
		urlPatterns = "/*"
		)*/
public class BasicServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * Starts this Servlet.
	 */
	public void init() throws ServletException {
		System.out.println("starting...");
	}
	/**
	 * Handles a get request.
	 * @param HttpServletRequest request The request Object
	 * @param HttpServletResponse response The response object
	 * @exception Exception e An exception that may be thrown by the Servlet manager or IO issues.
	 */
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		response.getOutputStream().print("Hello world!");
		response.getOutputStream().print("This was generated by the server.");
		response.getOutputStream().close();
	}
	/**
	 * Closes this servlet
	 */
	public void close() {
		
	}

}

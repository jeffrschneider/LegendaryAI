package ai.legendary.services;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.infrastructure.SpeedConcat;
@WebServlet(
		urlPatterns = "/*"
		)
public class PageServlet extends HttpServlet {
	String[] pagenames = null;
	String[] pages = null;
	private static final long serialVersionUID = 1L;
	private static final String page404 = "404 Error";
	//private static final String pageDirectory = "C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\WordTupleConnection\\WebContent\\WEB-INF\\classes\\ai\\legendary\\services\\";
	//That's for testing.
	private static final String pageDirectory = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\WordTupleConnection\\WEB-INF\\classes\\ai\\legendary\\services\\";
	//This is for production
	private final static String readFile(final String fileName) {
		try {
			final SpeedConcat sc = new SpeedConcat();
			final Scanner input = new Scanner(new File(fileName));
			while (input.hasNextLine()) {
				sc.append(input.nextLine()).append("\r\n");
			}
			input.close();
			return sc.concat();
		} catch (final Exception e) {
			return "File Reading Error: " + fileName;
		}
	}
	/**
	 * This function is called by the Servlet manager, "which is usually Tomcat", when this Server is started. It performs tasks that need to be performed when this is happening.
	 */
	public void init() throws ServletException {
		pages = new String[1];
		pagenames = new String[1];
		pagenames[0] = "index.html";
		for (int index = 0; index < pagenames.length; index++) {
			pages[index] = readFile(pageDirectory + pagenames[index]);
		}
		System.out.println("starting...");
	}
	/**
	 * This function is called by the Servlet manager, "which is usually Tomcat", when this Server is shut down. It performs tasks that need to be performed when this is happening.
	 */
	public void destroy(){
		System.out.println("shutting down...");
	}
	public void doGet(final HttpServletRequest request, final HttpServletResponse response){
		for (int index = 0; index < pagenames.length; index++) {
			final String[] parts = request.getRequestURI().split("/");
			if ((parts[parts.length-1]).equals("")) {
				parts[parts.length-1] = "index.html";
			}
			if (pagenames[index].equals(parts[parts.length-1])) {
				try {
					final PrintWriter output = response.getWriter();
					output.write(pages[index]);
					output.close();
					return;
				} catch (final Exception e) {
					return;
				}
			}
		}
		try {
			final PrintWriter output = response.getWriter();
			output.write(page404 + ": " + request.getRequestURI().substring(1));
			output.close();
			return;
		} catch (final Exception e) {
			return;
		}
	}
}

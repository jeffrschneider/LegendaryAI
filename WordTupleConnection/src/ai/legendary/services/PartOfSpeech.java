package ai.legendary.services;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.stringUtils;
import ai.legendary.infrastructure.ApachePartOfSpeechDetector;
import ai.legendary.infrastructure.HardCodedDirectory;
import ai.legendary.core.SpeedConcat;
@WebServlet(
		urlPatterns = "/POS/*"
		)
public class PartOfSpeech extends HttpServlet {
	private ApachePartOfSpeechDetector aposd = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Performs the tasks that need to be performed on the server's startup.
	 */
	public void init() {
		final String dir = HardCodedDirectory.result();
		try {
			aposd = new ApachePartOfSpeechDetector(dir + "en-pos-maxent.bin", dir + "en-token.bin");
		} catch (final Exception e) {
			init();
		}
	}
	/**
	 * Performs the tasks that need to be performed on the server's termination.
	 */
	public void destroy(){
		
	}
	private final void missingParameterJob (final String param, final HttpServletResponse response) throws IOException {
		response.getWriter().write("{\"statusCode\": -1, \"missingParam\": \"" + param + "\"}");
		response.getWriter().close();
	}
	/**
	 * Handles a request to either tokenize or identify the parts of speech
	 * @param HttpServletRequest request a request
	 * @param HttpServletResponse response a response
	 * @exception IOException E An exception that may arise from the response's PrintWriter object. 
	 */
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String sentence = request.getParameter("sentence");
		if (sentence==null) {
			missingParameterJob("sentence", response);
			return;
		}
		final String action = request.getParameter("action");
		if (action==null) {
			missingParameterJob("action", response);
			return;
		}
		try {
			final PrintWriter writer = response.getWriter();
			switch (action) {
				case "tokenize":
					writer.write("{\"statusCode\": 1, \"Apache\": [");
					final String[] words = aposd.tokenize(stringUtils.urlDecode(sentence));
					if (words.length > 0) {
						writer.write("\"");
						writer.write(words[0]);
						writer.write("\"");
					}
					for(int index = 0; index < words.length; index++) {
						writer.write(",\"");
						writer.write(words[index]);
						writer.write("\"");
					}
					writer.write("]}");
					writer.close();
					return;
				case "POSjob":
					writer.write("{\"statusCode\": 1, \"Apache\": ");
					writer.write(aposd.analyze(stringUtils.urlDecode(sentence), false));
					writer.write("}");
					writer.close();
					return;
				case "":
				default:
					writer.write("{\"statusCode\": -1, \"InvalidAction\": \"" + action + "\"}");
					writer.close();
					return;
			}
		} catch (final Exception e) {
			response.getWriter().write("{\"statusCode\": -1, \"Status\": \"Server Error\"}");
			response.getWriter().close();
		}
	}

}

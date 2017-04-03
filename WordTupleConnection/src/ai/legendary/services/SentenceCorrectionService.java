package ai.legendary.services;
import ai.legendary.core.WordTupleConnection;
import ai.legendary.core.stringUtils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.infrastructure.ConnectionBuilder;
import ai.legendary.infrastructure.QueryString;
import ai.legendary.infrastructure.QueryKit;
import ai.legendary.infrastructure.SentenceCorrector;
import ai.legendary.core.SpeedConcat;
@WebServlet(
		urlPatterns = "/wordCorrect/*"
		)
public class SentenceCorrectionService extends HttpServlet {
	private static final long serialVersionUID = 2L;

	/**
	 * This function is called by the Servlet manager, "which is usually Tomcat", when this Server is shut down. It performs tasks that need to be performed when this is happening.
	 */
	public void destroy(){
		System.out.println("shutting down...");
	}
	/**
	 * This function is called by the Servlet manager, "which is usually Tomcat", when this Server is started. It performs tasks that need to be performed when this is happening.
	 */
	public void init() throws ServletException {
		System.out.println("starting...");
	}
	private final String resultGen (final String input, final String output, Integer[] points, String[] words) {
		final SpeedConcat result = 
			new SpeedConcat().append("{ \"statusCode\": 1, \"input\": \"").append(input).append("\", \"output\": \"").append(output).append("\", \"points\": [").append(points[0]);
		for (int index = 1; index < points.length; index++) {
			result.append(", ").append(points[index]);
		}
		result.append("], \"words\": [\"").append(words[0]);
		for (int index = 1; index < words.length; index++) {
			result.append("\", \"" + words[index]);
		}
		return result.append("\"] } ").concat();
	}
	/**
	 * Handles a request for a correction job.
	 * @param HttpServletRequest request The request object.
	 * @param HttpServletResponse response The response object.
	 */
	public void doGet(final HttpServletRequest request, final HttpServletResponse response){
		try {
			response.setHeader("Access-Control-Allow-Origin", "*");
			final PrintWriter writer = response.getWriter();
			final WordTupleConnection wtc = ConnectionBuilder.build();
			final QueryString qs = new QueryString(request.getQueryString());
			final SentenceCorrector sc = new SentenceCorrector(wtc);
			final String input = qs.get("flawedString");
			final String corrected = sc.getBestEffortFix(input);
			final String[] Sentence = stringUtils.convertToSentence(input);
			writer.write(resultGen(input, corrected, sc.getQueryKit().getFailurePoints(Sentence), Sentence));
			writer.close();
			wtc.close();
		} catch (final Exception e) {
			try {
				e.printStackTrace(response.getWriter());
				response.getWriter().print("{\"statusCode\": -1}");
				response.getWriter().close();
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}

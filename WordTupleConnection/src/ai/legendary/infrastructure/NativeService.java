package ai.legendary.infrastructure;
import javax.servlet.*;
import javax.servlet.http.*;

import ai.legendary.core.SpeedConcat;
import ai.legendary.core.WordTupleConnection;
import ai.legendary.core.stringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.annotation.WebServlet;
/*@WebServlet(
		urlPatterns = "/*"
		)*/
public class NativeService extends HttpServlet {
	private static final long serialVersionUID = 3L;
	/**
	 * This function is called by the Servlet manager, "which is usually Tomcat", when this Server is started. It performs tasks that need to be performed when this is happening.
	 */
	public void init() throws ServletException {
		System.out.println("starting...");
	}
	private static final String legacyPageGen (final String input, final String output) {
		return
			("<!DOCTYPE html><html><head><title>Proof of concept</title><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js\"></script></head><body><textarea id=\"input\">" + input) + ("</textarea><button id=\"attempt\">fix</button><p id=\"output\">" + output) + "</p><script>\"use strict\";const encode = function(str) {return encodeURIComponent(str).replace(/[!'()*]/g, function(c) {return '%' + c.charCodeAt(0).toString(16);});};$(document).ready(function(){$(\"#attempt\").click(function(){window.location.assign(window.location.href.split(\"?\")[0] + \"?flawedString=\" + encode(document.getElementById(\"input\").value));});});</script></body></html>";
	}
	private static final String pageGen (final String input, final String output, final Integer[] errorIndexes, final String[] words) {
		final SpeedConcat sc = new SpeedConcat().append("<!DOCTYPE html><html><head><title>Proof of concept</title><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js\"></script></head><body><textarea id=\"input\">");
		sc.append(input).append("</textarea><button id=\"attempt\">fix</button><p id=\"output\">").append(output);
		sc.append("</p><script>\"use strict\";const encode = function(str) {return encodeURIComponent(str).replace(/[!'()*]/g, function(c) {return '%' + c.charCodeAt(0).toString(16);});};$(document).ready(function(){$(\"#attempt\").click(function(){window.location.assign(window.location.href.split(\"?\")[0] + \"?flawedString=\" + encode(document.getElementById(\"input\").value));});});</script>");
		sc.append("<table><thead><tr><th>Index</th><th>Word 1</th><th>Word 2</th></tr></thead><tbody>");
		for (int index = 0; index < errorIndexes.length; index++) {
			final int ei = errorIndexes[index].intValue()-1;
			sc.append("<tr><td>").append(ei).append("</td><td>").append(words[ei]).append("</td><td>").append(words[ei+1]).append("</td></tr>");
		}
		return sc.append("</tbody></table></body></html>").concat();
	}
	private static final String errPageGen(final String input) {
		if (input==null) {
			return errPageGen("Exception");
		}
		return "<!DOCTYPE html><html><head><title>Error</title></head><body>An error has happened: " + (input + "</body></html>");
	}
	/**
	 * This function is called by the Servlet manager, "which is usually Tomcat", when an HTTP GET request is received. This responds with an HTML page which is either an error page, or the console for attempting to correct sentences.
	 * @param HttpServletRequest request The request Object of the HTTP request. This is documented by Tomcat.
	 * @param HttpServletResponse response The request Object of the HTTP request. This is documented by Tomcat.
	 */
	public void doGet(final HttpServletRequest request, final HttpServletResponse response){
		try {
			final PrintWriter writer = response.getWriter();
			final WordTupleConnection wtc = ConnectionBuilder.build();
			final QueryString qs = new QueryString(request.getQueryString());
			final SentenceCorrector sc = new SentenceCorrector(wtc);
			final String input = qs.get("flawedString");
			final String corrected = sc.getBestEffortFix(input);
			//writer.write(legacyPageGen(input, corrected));
			final String[] Sentence = stringUtils.convertToSentence(input);
			writer.write(pageGen(input, corrected, sc.getQueryKit().getFailurePoints(Sentence), Sentence));
			writer.close();
			wtc.close();
		} catch (final Exception e) {
			try {
				e.printStackTrace(response.getWriter());
				response.getWriter().write(errPageGen(e.getMessage()));
				response.getWriter().close();
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	/**
	 * This function is called by the Servlet manager, "which is usually Tomcat", when this Server is shut down. It performs tasks that need to be performed when this is happening.
	 */
	public void destroy(){
		System.out.println("shutting down...");
	}
}

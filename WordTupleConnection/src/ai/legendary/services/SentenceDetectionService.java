package ai.legendary.services;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.stringUtils;
import ai.legendary.infrastructure.DynamicSentenceDetector;
import opennlp.tools.util.Span;
import ai.legendary.core.SpeedConcat;
import ai.legendary.infrastructure.HardCodedDirectory;
@WebServlet(
		urlPatterns = "/sentenceDetect/*"
		)
public class SentenceDetectionService extends HttpServlet {
	private DynamicSentenceDetector dsd = null;
	private int addIndex = 0;
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException{
		final String action = request.getParameter("action");
		if (action==null) {
			response.getWriter().print("{\"statusCode\": -1, \"Description\":\"Action not specified\"}");
			response.getWriter().close();
			return;
		}
		try {
			switch (action) {
				case "AddTrainingSentence":
					final String inputSentence = request.getParameter("input");
					if (inputSentence==null) {
						response.getWriter().print("{\"statusCode\": -1, \"Description\":\"Sentence not specified\"}");
						response.getWriter().close();
						return;
					}
					response.getWriter().print("{\"statusCode\": 1, \"Description\":\"Sentence added\"}");
					response.getWriter().close();
					dsd.AddString(stringUtils.urlDecode(inputSentence));
					addIndex++;
					if (addIndex > 1024) {
						addIndex = 0;
						dsd.reload();
					}
					return;
				case "getSentenceIndexes":
					final String inputSentenceB = request.getParameter("input");
					if (inputSentenceB==null) {
						response.getWriter().print("{\"statusCode\": -1, \"Description\":\"Sentences not specified\"}");
						response.getWriter().close();
						return;
					}
					final Span[] resultset = dsd.sentPosDetect(stringUtils.urlDecode(inputSentenceB));
					final SpeedConcat output = (new SpeedConcat()).append("{\"statusCode\": 1, \"results\": [");
					if (resultset.length > 0) {
						output.append("{[").append(resultset[0].getStart()).append(",").append(resultset[0].getEnd()).append("]}");
					}
					for (int index = 1; index < resultset.length; index++) {
						output.append(", {[").append(resultset[index].getStart()).append(",").append(resultset[index].getEnd()).append("]}");
					}
					response.getWriter().print(output.append("]}").concat());
					response.getWriter().close();
					return;
				case "":
				default:
					response.getWriter().print("{\"statusCode\": -1, \"Description\":\"Action not specified properly\"}");
					response.getWriter().close();
					return;
			}
		} catch (final Exception e) {
			response.getWriter().print("{\"statusCode\": -1, \"Description\":\"Server side exception\"}");
			response.getWriter().close();
			return;
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This function is called by the Servlet manager, "which is usually Tomcat", when this Server is started. It performs tasks that need to be performed when this is happening. It preloads all of the files needed by this module.
	 */
	public void init() throws ServletException {
		try {
			dsd = new DynamicSentenceDetector(HardCodedDirectory.result() + "SentenceCalibrationData.txt");
		} catch (final Exception e) {
			init();
		} 
	}
	/**
	 * This function is called by the Servlet manager, "which is usually Tomcat", when this Server is shut down. It performs tasks that need to be performed when this is happening.
	 */
	public void destroy(){
		System.out.println("shutting down...");
	}
}

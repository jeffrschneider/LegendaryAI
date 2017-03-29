package ai.legendary.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.stringUtils;
import ai.legendary.infrastructure.ApachePartOfSpeechDetector;
import ai.legendary.infrastructure.GeneralLemmatizer;
import ai.legendary.infrastructure.HardCodedDirectory;
import ai.legendary.infrastructure.LemmaResult;

@WebServlet(
		urlPatterns = "/LemmaService/*"
		)
public class LemmatizerService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GeneralLemmatizer gl = null;
	ApachePartOfSpeechDetector aposd = null;
	public void init() throws ServletException {
		final String dir = HardCodedDirectory.result();
		try {
			gl = new GeneralLemmatizer(dir + "en-lemmatizer.dict");
			aposd = new ApachePartOfSpeechDetector(dir + "en-pos-maxent.bin", dir + "en-token.bin");
		} catch (final IOException e) {
			init();
		}
	}
	public void destroy(){}
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException{
		final String action = request.getParameter("action");
		if (action==null) {
			MissingParam.missingParamJob("action", response);
			return;
		}
		final String rawTokens = request.getParameter("tokens");
		if (rawTokens==null) {
			MissingParam.missingParamJob("tokens", response);
			return;
		}
		try {
			final String[] tokens = rawTokens.split(",");
			for (int index = 0; index < tokens.length; index++) {
				tokens[index] = stringUtils.urlDecode(tokens[index]);
			}
			final String[] pos = aposd.tag(tokens);
			final String output = LemmaResult.JSONexport(gl.Lemmatize(tokens, pos), "\"statusCode\": 1, \"responses\": \"Apache\"");
			response.getWriter().write(output);
			response.getWriter().close();
		} catch (final Exception e) {
			MissingParam.serverError(response);
		}
	}

}

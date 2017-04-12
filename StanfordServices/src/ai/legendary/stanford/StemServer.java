package ai.legendary.stanford;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.stringUtils;

@WebServlet(
		urlPatterns = "/3.7.0/Stem/*"
		)
public class StemServer extends HttpServlet{
	public static final void main (final String[] args) {
		System.out.println("This is just a filler method so that Eclipse will do its job when it generates the buildfile.");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StemService ss = null;
	public void init(){
		ss = new StemService();
	}
	public void destroy(){}
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String rtext = request.getParameter("text");
		if (rtext==null) {
			MissingParam.missingParamJob("text", response);
			return;
		}
		try {
			final String text = stringUtils.urlDecode(rtext);
			final String[] tokens = ss.tokenize(text);
			final String[] stems = ss.getStems(tokens);
			MissingParam.stemResult(response, tokens, stems);
			return;
		} catch (final Exception e) {
			MissingParam.error("secondary processing error", response);
			return;
		}
	}
}

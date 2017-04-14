package ai.legendary.apache;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.stringUtils;

@WebServlet(
		urlPatterns = "/1.7.2/POS/*"
		)
public class ApachePartOfSpeechService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApachePOS apos = null;
	@Override
	public void init() throws ServletException {
		try {
			reload();
		} catch (final Exception e) {
			init();
		}
	}
	private final void reload() throws Exception {
		final ApacheTokenize at = new ApacheTokenize();
		apos = new ApachePOS(DataDir.result() + "POS.bin", at);
	}
	public void destory() {}
	@Override
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		try {
			final String rtext = request.getParameter("text");
			if (rtext==null) {
				MissingParam.missingParamJob("text", response);
				return;
			}
			final String text = stringUtils.urlDecode(rtext);
			final String[] tokens = apos.tokenize(text);
			final String[] poses = apos.tag(tokens);
			MissingParam.posResult(response, tokens, poses);
			return;
		} catch (final Exception e) {
			MissingParam.error(e.toString(), response);
			return;
		}
		
	}
	
}

package ai.legendary.apache;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.stringUtils;

@WebServlet(
		urlPatterns = "/1.7.2/Tokenize/*"
		)
public class TokenService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApacheTokenize at = null;
	private Exception loadFailure = null;
	@Override
	public void init(){
		try {
			at = new ApacheTokenize();
		} catch (final Exception e) {
			loadFailure = e;
		}
	}
	@Override
	public void destroy(){}
	@Override
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		if (loadFailure != null) {
			MissingParam.error("Server startup error", response);
			return;
		}
		final String raw = request.getParameter("text");
		if (raw==null) {
			MissingParam.missingParamJob("text", response);
			return;
		}
		try {
			final String text = stringUtils.urlDecode(raw);
			final String[] tokens = at.tokenize(text);
			MissingParam.tokenResult(response, tokens);
			return;
		} catch (final Exception e) {
			MissingParam.error("tokenization error", response);
			return;
		}
	}
}

package ai.legendary.IES;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.InformationExtractorResult;
import ai.legendary.core.MissingParam;
import ai.legendary.core.stringUtils;
@WebServlet(
		urlPatterns = "/1/Claus/*"
		)
public class ClausService extends HttpServlet {
	private final clausIE cie = new clausIE();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void init(){
		
	}
	public void destroy(){
		
	}
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String raw = request.getParameter("text");
		if (raw==null) {
			MissingParam.missingParamJob("text", response);
			return;
		}
		try {
			final String text = stringUtils.urlDecode(raw);
			final InformationExtractorResult[] iers = cie.determine(text);
			MissingParam.IEresult(response, iers, false);
			return;
		} catch (final Exception e) {
			MissingParam.error("Processing error", response);
			return;
		}
	}
}

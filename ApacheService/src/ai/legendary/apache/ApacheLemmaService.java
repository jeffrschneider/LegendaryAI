package ai.legendary.apache;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.stringUtils;
@WebServlet(
		urlPatterns = "/1.7.2/Lemma/*"
		)
public class ApacheLemmaService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Exception e = null;
	private ApacheLemma lemma = null;
	public void init() {
		try {
			final String dir = DataDir.result();
			final String a = dir + "tokenizer.bin";
			final String b = dir + "POS.bin";
			final String c = dir + "lemma.bin";
			lemma = new ApacheLemma(a, b, c);
		} catch (final Exception f) {
			e = f;
		}
	}
	public void destroy(){}
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String text = request.getParameter("text");
		if (text==null) {
			MissingParam.missingParamJob("text", response);
			return;
		}
		if (e != null ) {
			e.printStackTrace(response.getWriter());
			//MissingParam.serverError(response);
			return;
		}
		try {
			final String[] tokens = lemma.tokenize(stringUtils.urlDecode(text));
			final String[] poses = lemma.posJob(tokens);
			final String[] lemmas = lemma.lemmatize(tokens, poses);
			MissingParam.lemmaResult(response, tokens, poses, lemmas);
			return;
		} catch (final Exception f) {
			f.printStackTrace(response.getWriter());
			MissingParam.serverError(response);
			return;
		} 
	}

}

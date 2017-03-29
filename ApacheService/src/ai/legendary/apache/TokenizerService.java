package ai.legendary.apache;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.stringUtils;

@WebServlet(
		urlPatterns = "/1.7.2/Tokenizer/*"
		)
public class TokenizerService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ApacheTokenizer at = null;
	public void init(){
		try {
			at = new ApacheTokenizer(DataDir.result() + "tokenizerData.txt");
		} catch (final Exception e) {
			init();
		}
	}
	public void destory() {}
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final PrintWriter pw = response.getWriter();
		final String action = request.getParameter("action");
		if (action==null) {
			MissingParam.missingParamJob("action", response);
			return;
		}
		try {
			switch (action) {
				case "tokenize":
					final String target = request.getParameter("input");
					if (target==null) {
						MissingParam.missingParamJob("input", response);
						return;
					}
					final String[] words = at.tokenize(stringUtils.urlDecode(target));
					pw.print("{\"statusCode\": 1,\"results\":[");
					if (words.length > 0) {
						pw.print("\"");
						pw.print(stringUtils.join(words[0].split("\""), "\\\""));
						pw.print("\"");
					}
					for (int index = 1; index < words.length; index++) {
						pw.print(",\"");
						pw.print(stringUtils.join(words[index].split("\""), "\\\""));
						pw.print("\"");
					}
					pw.print("]}");
					pw.close();
					return;
				case "submitCorrection":
					final String rawcor = request.getParameter("correction");
					if (rawcor==null) {
						MissingParam.missingParamJob("correction", response);
						return;
					}
					at.submitCorrection(stringUtils.urlDecode(rawcor));
					MissingParam.succeededUpdate(response);
					return;
				case "makeCorrection":
					final String fixer = request.getParameter("input");
					if (fixer==null) {
						MissingParam.missingParamJob("input", response);
						return;
					}
					final String indexes = request.getParameter("indexes");
					if (indexes==null) {
						MissingParam.missingParamJob("indexes", response);
						return;
					}
					final int[] points =  stringUtils.StringToIntArray(stringUtils.urlDecode(indexes));
					final String result = ApacheTokenizer.correctionBuilder(stringUtils.urlDecode(fixer), points);
					pw.write("{\"statusCode\": 1,\"results\":\"");
					pw.write(result.replaceAll("\"", "\\\""));
					pw.write("\"}");
					pw.close();
					return;
				default:
					MissingParam.missingParamJob("action", response);
					return;
			}
		} catch (final Exception e) {
			MissingParam.error(e.toString(), response);
			return;
		}
	}
}

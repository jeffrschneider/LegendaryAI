package ai.legendary.apache;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
		urlPatterns = "/1.7.2/POS/*"
		)
public class ApachePartOfSpeechService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApachePOS apos = null;
	public void init() throws ServletException {
		try {
			reload();
		} catch (final Exception e) {
			init();
		}
	}
	private final void reload() throws Exception {
		final ApacheTokenizer at = new ApacheTokenizer(DataDir.result() + "tokenizerData.txt");
		apos = new ApachePOS(DataDir.result() + "POS.bin", at);
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
				case "pos":
					final String rtext = request.getParameter("text");
					if (rtext==null) {
						MissingParam.missingParamJob("text", response);
						return;
					}
					final String text = stringUtils.urlDecode(rtext);
					final String[] tokens = apos.tokenize(text);
					final String[] poses = apos.tag(tokens);
					pw.write("{\"statusCode\": 1, \"tokens\":[");
					if (tokens.length > 0) {
						pw.write("\"");
						pw.write(tokens[0].replaceAll("\"", "\\\""));
						pw.write("\"");
					}
					for (int index = 0; tokens.length > index; index++) {
						pw.write(", \"");
						pw.write(tokens[index].replaceAll("\"", "\\\""));
						pw.write("\"");
					}
					pw.write("], \"POS\": [");
					if (poses.length > 0) {
						pw.write("\"");
						pw.write(poses[0].replaceAll("\"", "\\\""));
						pw.write("\"");
					}
					for (int index = 0; poses.length > index; index++) {
						pw.write(", \"");
						pw.write(poses[index].replaceAll("\"", "\\\""));
						pw.write("\"");
					}
					pw.write("]}");
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

package ai.legendary.chunker;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
		urlPatterns = "/*"
		)
public class ChunkerService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApacheChunker apache = null;
	private boolean serverFault = false;
	private Exception workingError = null;
	public void init() throws ServletException {
		final String dir = DataDir.result();
		try {
			apache = ApacheChunker.BuildApacheChunkerFromBins(dir + "apacheTokens.bin", dir + "apachePOS.bin", dir + "apacheChunks.bin");
		} catch (final Exception e) {
			serverFault = true;
			workingError = e;
		}
	}
	public void destroy(){
		
	}
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		if (serverFault) {
			//workingError.printStackTrace(response.getWriter());
			//response.getWriter().close();
			MissingParam.serverError(response);
			return;
		}
		final String raw = request.getParameter("text");
		if (raw==null) {
			MissingParam.missingParamJob("text", response);
			return;
		}
		try {
			final String text = stringUtils.urlDecode(raw);
			final PrintWriter output = response.getWriter();
			output.write("{\"statusCode\": 1, \"results\": [");
			output.write(ChunkerResult.jsonExport(apache.chunk(text)));
			output.write("]}");
			output.close();
		} catch (final Exception e) {
			//e.printStackTrace(response.getWriter());
			//response.getWriter().close();
			MissingParam.serverError(response);
		}
	}

}

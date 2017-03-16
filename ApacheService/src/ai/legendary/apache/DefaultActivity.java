package ai.legendary.apache;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
@WebServlet(
		urlPatterns = "/*"
		)
public class DefaultActivity extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void init() throws ServletException {}
	public void destory() {}
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": -1,\"problem\": \"invalidService\",\"services\": [{\"name\": \"Apache\",\"versions\": [\"1.7.2\"],\"services\": [\"SentenceDetect\", \"Tokenizer\", \"NounDetect\"]}]}");
		writer.close();
	}
	
}

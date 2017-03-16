package ai.legendary.services;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class StandardizedResponses {
	public static final void missingParameterJob (final String param, final HttpServletResponse response) throws IOException {
		response.getWriter().write("{\"statusCode\": -1, \"missingParam\": \"" + param + "\"}");
		response.getWriter().close();
	}
	public static final void serverFailure (final HttpServletResponse response) throws IOException {
		response.getWriter().write("{\"statusCode\": -1, \"problem\": \"Server Error\"}");
		response.getWriter().close();
		return;
	}
}

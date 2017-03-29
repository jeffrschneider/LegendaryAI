package ai.legendary.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class MissingParam {
	public static final void missingParamJob(final String param, final HttpServletResponse response) throws IOException {
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": -1,\"problem\": \"missingParam\",\"param\": \"");
		writer.write(param);
		writer.write("\"}");
		writer.close();
	}
	public static final void error (final String err, final HttpServletResponse response) throws IOException {
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": -1,\"problem\": \"");
		writer.write(err);
		writer.write("\"}");
		writer.close();
	}
	public static final void serverError (final HttpServletResponse response) throws IOException {
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": -1,\"problem\": \"server error\"}");
		writer.close();
	}
	public static final void succeededUpdate(final HttpServletResponse response) throws IOException {
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"message\": \"updated\"}");
		writer.close();
	}
	public static final void processing(final HttpServletResponse response) throws IOException {
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 0,\"message\": \"processing\"}");
		writer.close();
	}
}

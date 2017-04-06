package ai.legendary.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class MissingParam {
	public static final void missingParamJob(final String param, final HttpServletResponse response) throws IOException {
		final PrintWriter writer = response.getWriter();
		response.setHeader("Access-Control-Allow-Origin", "*");
		writer.write("{\"statusCode\": -1,\"problem\": \"missingParam\",\"param\": \"");
		writer.write(param);
		writer.write("\"}");
		writer.close();
	}
	public static final void error (final String err, final HttpServletResponse response) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": -1,\"problem\": \"");
		writer.write(err);
		writer.write("\"}");
		writer.close();
	}
	public static final void serverError (final HttpServletResponse response) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": -1,\"problem\": \"server error\"}");
		writer.close();
	}
	public static final void succeededUpdate(final HttpServletResponse response) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"message\": \"updated\"}");
		writer.close();
	}
	public static final void processing(final HttpServletResponse response) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 0,\"message\": \"processing\"}");
		writer.close();
	}
	public static final void lemmaResult(final HttpServletResponse response, final String[] tokens, final String[] parts, final String[] lemmas) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"tokens\": [");
		if (tokens.length > 0) {
			writer.write("\"");
			writer.write(tokens[0]);
			writer.write("\"");
		}
		for (int index = 1; index < tokens.length; index++) {
			writer.write(", \"");
			writer.write(tokens[index]);
			writer.write("\"");
		}
		writer.write("], \"POS\": [");
		if (parts.length > 0) {
			writer.write("\"");
			writer.write(parts[0]);
			writer.write("\"");
		}
		for (int index = 1; index < parts.length; index++) {
			writer.write(", \"");
			writer.write(parts[index]);
			writer.write("\"");
		}
		writer.write("], \"lemma\": [");
		if (lemmas.length > 0) {
			writer.write("\"");
			writer.write(lemmas[0]);
			writer.write("\"");
		}
		for (int index = 1; index < lemmas.length; index++) {
			writer.write(", \"");
			writer.write(lemmas[index]);
			writer.write("\"");
		}
		writer.write("]}");
		writer.close();
	}
	public static final void stemResult(final HttpServletResponse response, final String[] tokens, final String[] stems) throws IOException{
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"tokens\": [");
		if (tokens.length > 0) {
			writer.write("\"");
			writer.write(tokens[0]);
			writer.write("\"");
		}
		for (int index = 1; index < tokens.length; index++) {
			writer.write(", \"");
			writer.write(tokens[index]);
			writer.write("\"");
		}
		writer.write("], \"stems\": [");
		if (stems.length > 0) {
			writer.write("\"");
			writer.write(stems[0]);
			writer.write("\"");
		}
		for (int index = 1; index < stems.length; index++) {
			writer.write(", \"");
			writer.write(stems[index]);
			writer.write("\"");
		}
		writer.write("]}");
		writer.close();
	}
}

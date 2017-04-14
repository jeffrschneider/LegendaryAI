package ai.legendary.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

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
	public static final void SROsetResult(final HttpServletResponse response, final LinkedList<SROset> sets) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"sets\": [");
		if (sets.size() > 0) {
			writer.write(SROset.toJSON(sets.removeFirst()));
		}
		while (sets.size() > 0) {
			writer.write(", ");
			writer.write(SROset.toJSON(sets.removeFirst()));
		}
		writer.write("]}");
		writer.close();
	}
	public static final void lemmaResult(final HttpServletResponse response, final String[] tokens, final String[] parts, final String[] lemmas) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"tokens\": [");
		stringUtils.printMembers(tokens, writer);
		writer.write("], \"POS\": [");
		stringUtils.printMembers(parts, writer);
		writer.write("], \"lemma\": [");
		stringUtils.printMembers(lemmas, writer);
		writer.write("]}");
		writer.close();
	}
	public static final void posResult(final HttpServletResponse response, final String[] tokens, final String[] parts) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"tokens\": [");
		stringUtils.printMembers(tokens, writer);
		writer.write("], \"POS\": [");
		stringUtils.printMembers(parts, writer);
		writer.write("]}");
		writer.close();
	}
	public static final void stemResult(final HttpServletResponse response, final String[] tokens, final String[] stems) throws IOException{
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"tokens\": [");
		stringUtils.printMembers(tokens, writer);
		writer.write("], \"stems\": [");
		stringUtils.printMembers(stems, writer);
		writer.write("]}");
		writer.close();
	}
	public static final void tokenResult(final HttpServletResponse response, final String[] tokens) throws IOException{
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"tokens\": [");
		stringUtils.printMembers(tokens, writer);
		writer.write("]}");
		writer.close();		
	}
	public static final void sentenceResult(final HttpServletResponse response, final String[] results) throws IOException{
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"statusCode\": 1,\"results\": [");
		stringUtils.printMembers(results, writer);
		writer.write("]}");
		writer.close();		
	}
	
}

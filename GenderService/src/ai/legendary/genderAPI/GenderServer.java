package ai.legendary.genderAPI;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.WordTupleConnection;
import ai.legendary.core.stringUtils;
import edu.stanford.nlp.ling.CoreLabel;
@WebServlet(
		urlPatterns = "/*"
		)
public class GenderServer extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GenderService gs = null;
	private Exception fail = null;
	public void init() {
		try {
			final WordTupleConnection con = WordTupleConnection.MakeWordTupleConnection("MasterUser", "MasterPassword", "ngrams.cbndyymb1za5.us-east-1.rds.amazonaws.com", "names", WordTupleConnection.databaseMode.MySQL);
			gs = new GenderService(con);
		} catch (final Exception e) {
			fail = e;
		}
	}
	public void destroy(){}
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		if (fail != null ) {
			MissingParam.serverError(response);
			//fail.printStackTrace(response.getWriter());
			//response.getWriter().close();
			return;
		}
		final String raw = request.getParameter("text");
		if (raw==null) {
			MissingParam.missingParamJob("text", response);
			return;
		}
		final String lang = request.getParameter("lang");
		if (lang==null) {
			MissingParam.missingParamJob("lang", response);
			return;
		}
		final String format = request.getParameter("format");
		if (format != null) {
			switch (format) {
				case "linear":
					linearJob(request, response);
					return;
				case "block":
					break;
				default:
					MissingParam.missingParamJob("format", response);
					return;
			}
		}
		try {
			final String text = stringUtils.urlDecode(raw);
			final LinkedList<NounObject> nos = gs.generateNounObjects(text, lang);
			final NounObject[] noset = nos.toArray(new NounObject[nos.size()]);
			final PrintWriter printer = response.getWriter();
			printer.print("{\"statusCode\": 1,\"results\": [");
			if (noset.length > 0) {
				noset[0].JSONprint(printer);
			}
			for (int index = 1; index < noset.length; index++) {
				printer.println(", ");
				noset[index].JSONprint(printer);
			}
			printer.print("]}");
			printer.close();
			return;
		} catch (final Exception e) {
			MissingParam.serverError(response);
			//e.printStackTrace(response.getWriter());
			//response.getWriter().close();
			return;
		}
	}
	private void linearJob(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String raw = request.getParameter("text");
		final String lang = request.getParameter("lang");
		try {
			final String text = stringUtils.urlDecode(raw);
			final LinkedList<CoreLabel> tokens = gs.tokenize(text, lang);
			final String[] tokenSet = new String[tokens.size()];
			final GenderService.genderData[] genders = new GenderService.genderData[tokens.size()];
			gs.populateGenders(tokens, genders, tokenSet, lang);
			final PrintWriter writer = response.getWriter();
			writer.write("{\"statusCode\": 1, \"tokens\": [");
			if (tokenSet.length > 0) {
				writer.write("\"");
				writer.write(tokenSet[0].replaceAll("\"", "\\\"").replaceAll("\'", "\\\'"));
				writer.write("\"");
			}
			for (int index = 1; tokenSet.length > index; index++) {
				writer.write(", \"");
				writer.write(tokenSet[index].replaceAll("\"", "\\\"").replaceAll("\'", "\\\'"));
				writer.write("\"");
			}
			writer.write("], \"genders\": [");
			if (genders.length > 0) {
				if (genders[0] != null) {
					writer.write("\"");
					writer.write(GenderService.genderExport(genders[0]));
					writer.write("\"");
				} else {
					writer.write("null");
				}
			}
			for (int index = 1; genders.length > index; index++) {
				writer.write(",");
				if (genders[index] != null) {
					writer.write(" \"");
					writer.write(GenderService.genderExport(genders[index]));
					writer.write("\"");
				} else {
					writer.write("null");
				}
			}
			writer.write("]}");
			writer.close();
		} catch (final Exception e) {
			
			//e.printStackTrace(response.getWriter());
			MissingParam.serverError(response);
			return;
		}
	}
}

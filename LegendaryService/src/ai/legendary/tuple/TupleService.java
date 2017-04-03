package ai.legendary.tuple;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.WordTupleConnection;
import ai.legendary.core.stringUtils;

@WebServlet(
		urlPatterns = "/1/TupleService/*"
		)
public class TupleService extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	WordTupleConnection wtc = null;
	Exception loaderFault = null;
	StanfordTokenizer st = null;
	QueryKit qk = null;
	public void init() {
		try {
			st = new StanfordTokenizer();
			wtc = WordTupleConnection.MakeWordTupleConnection("MasterUser", "MasterPassword", "ngrams.cbndyymb1za5.us-east-1.rds.amazonaws.com", "NGrams", WordTupleConnection.databaseMode.MySQL);
			qk = new QueryKit(wtc);
		} catch (final Exception e) {
			loaderFault = e;
		}	
	}
	public void destroy(){
		
	}
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException{
		response.setHeader("Access-Control-Allow-Origin", "*");
		final PrintWriter pw = response.getWriter();
		if (loaderFault != null){
			loaderFault.printStackTrace(pw);
			response.getWriter().close();
			return;
		}
		final String rawText = request.getParameter("text");
		final String rawN = request.getParameter("N");
		if (rawText==null) {
			MissingParam.missingParamJob("text", response);
			return;
		}
		if (rawN==null) {
			MissingParam.missingParamJob("N", response);
			return;
		}
		try {
			final int N = Integer.parseInt(rawN);
			LinkedList<NGramObject> ngrams = null;
			final String[] tokens = st.tokenize(stringUtils.urlDecode(rawText));
			switch (N) {
				case 2:
				case 3:
				case 4:
				case 5:
					ngrams = getSets(tokens, N);
					break;
				default:
					MissingParam.error("Invalid tuple count", response);
					return;
			}
			pw.print("{\"statusCode\": 1, \"results\":[");
			if (ngrams.size() > 0) {
				ngrams.removeFirst().print(pw);
			}
			while (ngrams.size() > 0) {
				pw.println(", ");
				ngrams.removeFirst().print(pw);
			}
			pw.print("]}");
			pw.close();
		} catch (final Exception e) {
			e.printStackTrace(pw);
			response.getWriter().close();
			return;
		}
	}
	private final LinkedList<NGramObject> getSets (final String[] tokens, final int N) throws Exception {
		final LinkedList<NGramObject> result = new LinkedList<NGramObject>();
		for (int index = N-1; index < tokens.length; index++) {
			final NGramObject ngo = new NGramObject();
			final String[] subTokens = Arrays.copyOfRange(tokens, index-N+1, index+1);
			ngo.setTokens(subTokens);
			innerSwitch:
			switch(N) {
				case 2:
					ngo.setPercentage(qk.ngram2Pct(subTokens[0], subTokens[1]));
					break innerSwitch;
				case 3:
					ngo.setPercentage(qk.ngram3Pct(subTokens[0], subTokens[1], subTokens[2]));
					break innerSwitch;
				case 4:
					ngo.setPercentage(qk.ngram4Pct(subTokens[0], subTokens[1], subTokens[2], subTokens[3]));
					break innerSwitch;
				case 5:
					ngo.setPercentage(qk.ngram5Pct(subTokens[0], subTokens[1], subTokens[2], subTokens[3], subTokens[4]));
					break innerSwitch;
			}
			result.add(ngo);
		}
		return result;
	}
}

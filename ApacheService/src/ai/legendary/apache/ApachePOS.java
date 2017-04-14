package ai.legendary.apache;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class ApachePOS {
	private POSTaggerME tagger = null;
	private ApacheTokenize at = null;
	
	public ApachePOS(final String input, final ApacheTokenize tkr) throws Exception {
		if (input==null) {
			throw new Exception();
		}
		if (tkr==null) {
			throw new Exception();
		}
		final InputStream modelIn = new FileInputStream(input);
		final POSModel model = new POSModel(modelIn);
		tagger = new POSTaggerME(model);
		at = tkr;
	}
	public final String[] tag(final String[] input) {
		return tagger.tag(input);
	}
	public final String[] tag(final String input) {
		return tagger.tag(at.tokenize(input));
	}
	public final String[] tokenize(final String input) {
		return (at.tokenize(input));
	}
	
}

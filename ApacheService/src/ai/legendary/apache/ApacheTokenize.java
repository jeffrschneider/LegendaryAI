package ai.legendary.apache;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class ApacheTokenize {
	private static final void nullCheck(final Object o) {
		if (o==null) {
			throw new NullPointerException();
		}
		return;
	}
	private Tokenizer tokenizer = null;
	public ApacheTokenize() throws Exception {
		final InputStream modelIn = new FileInputStream(DataDir.result() + "tokenizer.bin");
		final TokenizerModel model = new TokenizerModel(modelIn);
		tokenizer = new TokenizerME(model);
		modelIn.close();
	}
	public final String[] tokenize(final String input) {
		nullCheck(input);
		return tokenizer.tokenize(input);
	}
}

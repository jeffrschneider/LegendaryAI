package ai.legendary.infrastructure;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class ApachePartOfSpeechDetector {
	Tokenizer tokenizer = null;
	POSTaggerME tagger = null;
	public ApachePartOfSpeechDetector (final String modelDirectory, final String tokenizerDirectory) throws IOException {
		final InputStream modelIn = new FileInputStream(tokenizerDirectory);
		final InputStream modelIn2 = new FileInputStream(modelDirectory);
		final TokenizerModel model = new TokenizerModel(modelIn);
		final POSModel model2 = new POSModel(modelIn2);
		tokenizer = new TokenizerME(model);
		tagger = new POSTaggerME(model2);
		modelIn.close();
		modelIn2.close();
	}
	public final String[] analyze (final String input) {
		final String[] words = tokenizer.tokenize(input);
		return tagger.tag(words);
	}
	public final String[] tag(final String[] input) {
		return tagger.tag(input);
	}
	public final String analyze (final String input, final boolean garbage) {
		final String[] words = tokenizer.tokenize(input);
		return (new PartOfSpeechObject()).determine(tagger, words).JSONexport();
	}
	public final String[] tokenize(final String input) {
		return tokenizer.tokenize(input);
	}
}

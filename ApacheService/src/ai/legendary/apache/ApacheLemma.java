package ai.legendary.apache;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class ApacheLemma {
	Tokenizer tokenizer = null;
	POSTaggerME tagger = null;
	DictionaryLemmatizer lemmatizer = null;
	public ApacheLemma(final String tokenizerBin, final String posBin, final String lemmaBin) throws Exception {
		final InputStream tokenIn = new FileInputStream(tokenizerBin);
		final TokenizerModel tm = new TokenizerModel(tokenIn);
		tokenIn.close();
		tokenizer = new TokenizerME(tm);
		final InputStream posIn = new FileInputStream(posBin);
		final POSModel pm = new POSModel(posIn);
		posIn.close();
		tagger = new POSTaggerME(pm);
		final InputStream modelIn = new FileInputStream(lemmaBin);
		lemmatizer = new DictionaryLemmatizer(modelIn);
		modelIn.close();
	}
	private static void nullCheck(final Object o) throws NullPointerException{
		if (o==null) {
			throw new NullPointerException();
		}
	}
	public final String[] posJob(final String[] input) throws Exception{
		nullCheck(input);
		return tagger.tag(input);
	}
	public final String[] tokenize(final String input) throws Exception{
		nullCheck(input);
		return tokenizer.tokenize(input);
	}
	public final String[] lemmatize(final String[] tokens, final String[] parts) throws Exception{
		nullCheck(tokens);
		nullCheck(parts);
		return lemmatizer.lemmatize(tokens, parts);
	}
}

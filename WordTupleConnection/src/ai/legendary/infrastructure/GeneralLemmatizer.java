package ai.legendary.infrastructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.Lemmatizer;

public class GeneralLemmatizer {
	private Lemmatizer apache = null;
	public GeneralLemmatizer (final String Apache) throws IOException {
		final File initialFile = new File(Apache);
		final InputStream in = new FileInputStream(initialFile);
		apache = new DictionaryLemmatizer(in);
		in.close();
	}
	public final LemmaResult Lemmatize(final String[] word, final String[] postag) {
		final LemmaResult result = new LemmaResult();
		final String[] intermediate = apache.lemmatize(word, postag);
		for (int index = 0; index < intermediate.length; index++) {
			if (intermediate[index].equals("O")) {
				intermediate[index] = "";
			}
		}
		result.Apache = intermediate;
		return result;
	}
}

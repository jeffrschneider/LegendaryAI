package ai.legendary.infrastructure;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

public class CombinationApacheNameIdentifier {
	private NameFinderME[] nameFinders = null;
	public CombinationApacheNameIdentifier(final String[] files) throws Exception {
		nameFinders = new NameFinderME[files.length];
		for (int index = 0; index < files.length; index++) {
			final InputStream modelIn = new FileInputStream(files[index]);
			final TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
			modelIn.close();
			nameFinders[index] = new NameFinderME(model);
		}
	}
	public final Identifier[] getNames(final String[] input) throws Exception {
		final LinkedList<Identifier> result = new LinkedList<Identifier>();
		for (final NameFinderME nameFinder: nameFinders) {
			final Span[] spans = nameFinder.find(input);
			for (int index = 0; index < spans.length; index++) {
				result.add(Identifier.MakeIdentifier(spans[index], input));
			}
		}
		return (result.toArray(new Identifier[result.size()]));
	}
	public static final String JSONbuild (final String miscData, final Identifier[] input) {
		final Identifier[] input2 = input.clone();
		Arrays.sort(input2);
		final LinkedList<Identifier> data = new LinkedList<Identifier>();
		data.add(input2[0]);
		for (int index = 1; index < input2.length; index++) {
			if (!(input2[index].equals(input2[index-1]))) {
				data.add(input2[index]);
			}
		}
		return ApacheNameIdentifier.JSONbuild(miscData, data.toArray(new Identifier[data.size()]));
	}
}

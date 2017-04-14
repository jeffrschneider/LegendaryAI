package ai.legendary.apache;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

public class ApacheNameIdentifier {
	private final ArrayList<NameFinderME> finders = new ArrayList<NameFinderME>();
	private ApacheTokenize at = null;
	public ApacheNameIdentifier() throws Exception{
		at = new ApacheTokenize();
	}
	public ApacheNameIdentifier(final String src) throws Exception {
		at = new ApacheTokenize();
		addFinder(src);
	}
	public ApacheNameIdentifier(final String[] src) throws Exception {
		at = new ApacheTokenize();
		addFinder(src);
	}
	public ApacheNameIdentifier addFinder(final String input) throws Exception {
		if (input==null) {
			throw new Exception();
		}
		final InputStream modelIn = new FileInputStream(input);
		final TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
		modelIn.close();
		final NameFinderME nf = new NameFinderME(model);
		finders.add(nf);
		return this;
	}
	public ApacheNameIdentifier addFinder(final String[] input) throws Exception {
		if (input==null) {
			throw new Exception();
		}
		if (input.length==0) {
			throw new Exception();
		}
		for (final String s:input) {
			tryToAdd(s);
		}
		return this;
	}
	private boolean tryToAdd(final String input) {
		try {
			addFinder(input);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}
	public Noun[] findNames(final String input) throws Exception {
		if (input==null) {
			throw new Exception();
		}
		return findNames(at.tokenize(input));
	}
	public Noun[] findNames(final String[] input) throws Exception {
		if (input==null) {
			throw new Exception();
		}
		if (input.length==0) {
			throw new Exception();
		}
		final LinkedList<Noun> result = new LinkedList<Noun>();
		for (final NameFinderME nameFinder: finders) {
			final Span[] spans = nameFinder.find(input);
			for (int index = 0; index < spans.length; index++) {
				final Span s = spans[index];
				final String[] words = Arrays.copyOfRange(input, s.getStart(), s.getEnd());
				result.add(new Noun(words, s.getType()));
			}
		}
		result.sort(null);
		for (int index = result.size()-2; index >=0; index--) {
			if (result.get(index).equals(result.get(index+1))) {
				result.remove(index);
			}
		}
		return (result.toArray(new Noun[result.size()]));
	}
}

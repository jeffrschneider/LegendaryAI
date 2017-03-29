package ai.legendary.infrastructure;

import java.util.LinkedList;
import java.util.List;

import ai.legendary.core.SpeedConcat;
import opennlp.tools.postag.POSTaggerME;

public class PartOfSpeechObject {
	private LinkedList<String[]> resultSet = new LinkedList<String[]>();
	private LinkedList<double[]> odds = new LinkedList<double[]>();
	private String[] words = null;
	private void appendResult(final SpeedConcat sc, final int possible) {
		sc.append("{\"parts\":[");
		final String[] words = resultSet.get(possible);
		if (words.length > 0) {
			sc.append("\"").append(words[0]).append("\"");
		} 
		for (int index = 1; index < words.length; index++) {
			sc.append(",\"").append(words[index]).append("\"");
		}
		sc.append("], \"odds\":[");
		final double[] chances = odds.get(possible);
		if (chances.length > 0) {
			sc.append(chances[0]);
		} 
		for (int index = 1; index < chances.length; index++) {
			sc.append(",").append(chances[index]);
		}
		sc.append("]}");
		return;
	}
	/**
	 * A constructor
	 */
	public PartOfSpeechObject() {
		return;
	}
	/**
	 * Generates a JSON representation of the results of this POS tagging job
	 */
	public final String JSONexport(){
		final SpeedConcat sc = (new SpeedConcat()).append("{\"words\": [");
		if (words.length > 0) {
			sc.append("\"").append(words[0]).append("\"");
		}
		for (int index = 1; index < words.length; index++) {
			sc.append(",\"").append(words[index]).append("\"");
		}
		sc.append("], \"result\":[");
		if (resultSet.size() > 0) {
			appendResult(sc,0);
		}
		for (int index = 1 ; index < resultSet.size(); index++) {
			sc.append(",");
			appendResult(sc,index);
		}
		return sc.append("]}").concat();
	}
	/**
	 * Takes one of Apache's taggers, and an array of tokens as Strings, tags them, stores the results internally, and returns this.
	 * @param POSTaggerME tagger The tagger.
	 * @param String[] replacement The tokens
	 * @return PartOfSpeechObject this
	 */
	public final PartOfSpeechObject determine(final POSTaggerME tagger, final String[] replacement) {
		words = replacement;
		final opennlp.tools.util.Sequence[] sequences = tagger.topKSequences(words);
		final int upper = Integer.min(sequences.length, 1);
		for (int index = 0; index < upper; index++) {
			final opennlp.tools.util.Sequence sequence = sequences[index];
			final List<String> sl = sequence.getOutcomes();
			final double[] pl = sequence.getProbs();
			odds.add(pl);
			final String[] types = sl.toArray(new String[sl.size()]);
			resultSet.add(types);
		}
		return this;
	}
}

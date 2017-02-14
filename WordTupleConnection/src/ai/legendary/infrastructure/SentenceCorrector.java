package ai.legendary.infrastructure;
import java.util.LinkedList;
import java.math.BigDecimal;
public class SentenceCorrector {
	private WordTupleConnection connection = null;
	private QueryKit qk = null;
	public SentenceCorrector(final WordTupleConnection _connection) {
		connection = _connection;
		qk = new QueryKit(connection);
	}
	public String getBestEffortFix(final String inputSentence) throws Exception {
		final String[] sentence = stringUtils.convertToSentence(inputSentence);
		LinkedList<String> suggested = getBestEffortFix(sentence);
		final SpeedConcat result = new SpeedConcat();
		result.append(suggested.removeFirst());
		while (suggested.size() > 0) {
			result.append(" ");
			result.append(suggested.removeFirst());
		}
		return result.concat();
	}
	public LinkedList<String> getBestEffortFix(final String[] input) throws Exception {
		LinkedList<String> result = new LinkedList<String>();
		for (int index = 0; index < input.length; index++) {
			result.addLast(input[index]);
		}
		bodyLoop:
		while (true) {
			Integer[] failurePoints = qk.getFailurePoints(result.toArray(new String[result.size()]));
			if (failurePoints.length==0) {
				break bodyLoop;
			}
			if (failurePoints.length==1) {
				replaceMostLikely(result, failurePoints[0].intValue());
				continue bodyLoop;
			}
			if ((1+failurePoints[0].intValue())==(failurePoints[1].intValue())) {
				attemptInjection(result, failurePoints[1].intValue());
				continue bodyLoop;
			}
			replaceMostLikely(result, failurePoints[0].intValue());
			continue bodyLoop;
		}
		return result;
	}
	private void replaceMostLikely(LinkedList<String> words, final int index) throws Exception {
		if ((index > 1) && (index < (words.size()-1))) {
			final String[] a = qk.getMiddleWordFromNGram5(words.get(index-2), words.get(index-1), words.get(index+1), words.get(index+2));
			if (a != null) {
				words.set(index, a[0]);
				return;
			}
			final String[] b = qk.getMiddleWordFromNGram3(words.get(index-1), words.get(index+1));
			if (b != null) {
				words.set(index, b[0]);
				return;
			}
		}
		if ((index > 0) && (index < (words.size()-1)) ) {
			final String[] b = qk.getMiddleWordFromNGram3(words.get(index-1), words.get(index+1));
			if (b != null) {
				words.set(index, b[0]);
				return;
			}
		}
		if (index > 0) {
			final String[] d = qk.getFinalWordFromNGram2(words.get(index-1)); 
			if (d != null) {
				words.set(index, (d)[0]);
				return;
			}
		}
		switch (words.size()) {
			case 1:
				return;
			case 5:
			default:
				final String[] a = qk.getFirstWordFromNGram5(words.get(index+1), words.get(index+2), words.get(index+3), words.get(index+4));
				if (a != null) {
					words.set(index, a[0]);
					return;
				}
			case 4:
				final String[] b = qk.getFirstWordFromNGram4(words.get(index+1), words.get(index+2), words.get(index+3));
				if (b != null) {
					words.set(index, b[0]);
					return;
				}
			case 3:
				final String[] c = qk.getFirstWordFromNGram3(words.get(index+1), words.get(index+2));
				if (c != null) {
					words.set(index, c[0]);
					return;
				}
			case 2:
				words.set(index, (qk.getFirstWordFromNGram2(words.get(index+1)))[0]);
				return;
		}
		//return;
	}
	private void attemptInjection(LinkedList<String> words, final int index) throws Exception {
		if ((index > 1) && (index < (words.size()-1))) {
			final String[] a = qk.getMiddleWordFromNGram5(words.get(index-2), words.get(index-1), words.get(index), words.get(index+1));
			if (a != null) {
				words.add(index, a[0]);
				return;
			}
			final String[] b = qk.getMiddleWordFromNGram3(words.get(index-1), words.get(index));
			if (b != null) {
				words.add(index, b[0]);
				return;
			}
		}
		if ((index > 0) && (index < (words.size()-1)) ) {
			final String[] b = qk.getMiddleWordFromNGram3(words.get(index-1), words.get(index));
			if (b != null) {
				words.add(index, b[0]);
				return;
			}
		}
		if (index > 0) {
			final String[] d = qk.getFinalWordFromNGram2(words.get(index-1)); 
			if (d != null) {
				words.add(index, (d)[0]);
				return;
			}
		}
		switch (words.size()) {
			case 5:
			default:
				final String[] a = qk.getFirstWordFromNGram5(words.get(0), words.get(1), words.get(2), words.get(3)); 
				if (a != null) {
					words.add(index, (a)[0]);
					return;
				}
			case 4:
				final String[] b = qk.getFirstWordFromNGram4(words.get(0), words.get(1), words.get(2)); 
				if (b != null) {
					words.add(index, (b)[0]);
					return;
				}
			case 3:
				final String[] c = qk.getFirstWordFromNGram3(words.get(0), words.get(1)); 
				if (c != null) {
					words.add(index, (c)[0]);
					return;
				}
			case 2:
				final String[] d = qk.getFirstWordFromNGram2(words.get(0)); 
				if (d != null) {
					words.add(index, (d)[0]);
					return;
				}
			case 1:
			case 0:
				words.add("a");
				return;
		}
		//return;
	}
}

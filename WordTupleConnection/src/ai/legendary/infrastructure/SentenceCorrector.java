package ai.legendary.infrastructure;
import java.util.LinkedList;
import java.math.BigDecimal;
public class SentenceCorrector {
	private WordTupleConnection connection = null;
	private QueryKit qk = null;

	private int use2 = 0;
	private int use3 = 0;
	private int use4 = 0;
	private int use5 = 0;
	private int use2pct = 0;
	private int use3pct = 0;
	private int use4pct = 0;
	private int use5pct = 0;
	private int sentenceCount = 0;
	
	/**
	 * Returns information about what this instance of SentenceCorrector has had to do with various SQL jobs, as well as the number of times it's been used.
	 * @return String result The string of the information.
	 */
	public final String getstatinfo () {
		return
				((("get words from NGram5: " + use5 + "\r\n") + 
				("get words from NGram4: " + use4 + "\r\n")) + 
				(("get words from NGram3: " + use3 + "\r\n") + 
				("get words from NGram2: " + use2 + "\r\n"))) + 
				(((("get percent from NGram5: " + use5 + "\r\n") + 
				("get percent from NGram4: " + use4 + "\r\n")) + 
				(("get percent from NGram3: " + use3 + "\r\n") + 
				("get percent from NGram2: " + use2 + "\r\n"))) + 
				("sentenceCount: " + sentenceCount + "\r\n"))
				;
	}
	
	/**
	 * Returns the internal querykit for page generation.
	 * @return QueryKit qk the querykit.
	 */
	public QueryKit getQueryKit() {
		return qk;
	}
	
	/**
	 * A constructor. This takes the WordTupleConnection that is needed, and returns a new SentenceCorrector, which corrects bad word choice.
	 * @param WordTupleConnection _connection The connection
	 */
	public SentenceCorrector(final WordTupleConnection _connection) {
		connection = _connection;
		qk = new QueryKit(connection);
	}
	/**
	 * Takes a sentence in String format and returns an attempt at correcting the bad word choices of the sentence in String format.
	 * @param String inputSentence a sentence with bad word choices.
	 * @return String output. A sentence that may be better.
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public String getBestEffortFix(final String inputSentence) throws Exception {
		if (inputSentence.equals("")) {
			return "";
		}
		final String[] sentence = stringUtils.convertToSentence(inputSentence);
		LinkedList<String> suggested = getBestEffortFix(sentence);
		final SpeedConcat result = new SpeedConcat();
		result.append(suggested.removeFirst());
		while (suggested.size() > 0) {
			result.append(" ").append(suggested.removeFirst());
		}
		return result.concat();
	}
	/**
	 * Takes a sentence in array-of-Strings format and returns an attempt at correcting the bad word choices of the sentence in LinkedList<String> format.
	 * @param String[] inputSentence a sentence with bad word choices.
	 * @return LinkedList<String> output. A sentence that may be better.
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public LinkedList<String> getBestEffortFix(final String[] input) throws Exception {
		sentenceCount++;
		LinkedList<String> result = new LinkedList<String>();
		for (int index = 0; index < input.length; index++) {
			result.addLast(input[index]);
		}
		int oldHash = result.hashCode() - 1;
		bodyLoop:
		while (true) {
			Integer[] failurePoints = qk.getFailurePoints(result.toArray(new String[result.size()]));
			if (failurePoints.length==0) {
				break bodyLoop;
			}
			if (oldHash==result.hashCode()) {
				break bodyLoop;
			}
			oldHash = result.hashCode();
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
	private static final String getMostLikelyIntersection(final String[] a, final String[] b) {
		if (b==null) {
			if (a==null) {
				return "a";
			}
			return a[0];
		}
		if (a==null) {
			return b[0];
		}
		if (b.length==0) {
			if (a.length==0) {
				return "a";
			}
			return a[0];
		}
		if (a.length==0) {
			return b[0];
		}
		for (int indexA = 0; indexA < a.length; indexA++) {
			for (int indexB = 0; indexB < b.length; indexB++) {
				if (a[indexA].equals(b[indexB])) {
					return a[indexA];
				}
			}
		}
		return a[0];
	}
	private final String[] getFollowersFromWord(LinkedList<String> words, final int index) throws Exception {
		int following = (words.size() - index) - 1;
		if (following > 4) {
			following = 4;
		}
		String[] result = null;
		switch(following) {
			case 4:
				result = qk.getFirstWordFromNGram5(words.get(index+1), words.get(index+2), words.get(index+3), words.get(index+4));
				if (result != null) {
					use5++;
					return result;
				}
			case 3:
				result = qk.getFirstWordFromNGram4(words.get(index+1), words.get(index+2), words.get(index+3));
				if (result != null) {
					use4++;
					return result;
				}
			case 2:
				result = qk.getFirstWordFromNGram3(words.get(index+1), words.get(index+2));
				if (result != null) {
					use3++;
					return result;
				}
			case 1:
				result = qk.getFirstWordFromNGram2(words.get(index+1));		
				if (result != null) {
					use2++;
					return result;
				}	
			default:
			case 0:
				return new String[] {words.get(index)} ;
				
		}
	}
	private final String[] getPrecedersFromWord(LinkedList<String> words, final int index) throws Exception {
		final int refined = Math.min(index, 4);
		String[] result = null;
		switch(refined) {
			case 4:
				result = qk.getFinalWordFromNGram5(words.get(index-4), words.get(index-3), words.get(index-2), words.get(index-1));
				if (result != null) {
					use5++;
					return result;
				}
			case 3:
				result = qk.getFinalWordFromNGram4(words.get(index-3), words.get(index-2), words.get(index-1));
				if (result != null) {
					use4++;
					return result;
				}	
			case 2:
				result = qk.getFinalWordFromNGram3(words.get(index-2), words.get(index-1));
				if (result != null) {
					use3++;
					return result;
				}
			case 1:
				result = qk.getFinalWordFromNGram2(words.get(index-1));
				if (result != null) {
					use2++;
					return result;
				}	
			default:
			case 0:
				return new String[] {words.get(index)} ;
		}
	}
	private void replaceMostLikely(LinkedList<String> words, final int index) throws Exception {
		final String[] preceders = getPrecedersFromWord(words, index);
		final String[] followers = getFollowersFromWord(words, index);
		final String result = getMostLikelyIntersection(preceders, followers);
		words.set(index, result);
	}
	private void attemptInjection(LinkedList<String> words, final int index) throws Exception {
		final String[] preceders = getPrecedersFromWord(words, index);
		final String[] followers = getFollowersFromWord(words, index-1);
		final String result = getMostLikelyIntersection(preceders, followers);
		words.add(index, result);
	}
}

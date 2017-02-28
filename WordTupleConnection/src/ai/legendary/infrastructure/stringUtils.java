package ai.legendary.infrastructure;

import java.net.URLEncoder;
import java.net.URLDecoder;

public class stringUtils {
	/**
	 * A function which is identical to Java's URLEncoder.encode, except it doesn't have a special exception for space characters. It can also handle null values.
	 * @param String src The source String.
	 * @return String The result.
	 * @exception Exception E What happens if there's a REGEX error or a URLEncoder error.
	 */
	public static final String urlEncode(final String src) throws Exception{
		if (src==null) {
			return "";
		}
		return URLEncoder.encode(src, "UTF-8").replaceAll("\\+","%20");
	}
	/**
	 * A function which is identical to Java's URLDecoder.decdoe, except it doesn't have a special exception for space characters. It can also handle null values.
	 * @param String src The source String.
	 * @return String The result.
	 * @exception Exception E What happens if there's a REGEX error or a URLDecoder error.
	 */
	public static final String urlDecode(final String src) throws Exception {
		if (src==null) {
			return "";
		}
		return URLDecoder.decode(src.replaceAll("\\%20","\\+"), "UTF-8");
	}
	/**
	 * A utility function which takes a sentence and converts it into the array-of-words format that SentenceCorrector prefers. It also corrects case.
	 * @param String src the base string.
	 * @return String result the result.
	 */
	public static final String[] convertToSentence(final String src) {
		String phase2 = src.replaceAll("cannot","can not").replaceAll("\\,"," ").replaceAll("\\."," ").replaceAll("\\?"," ").replaceAll("\\@"," at ").replaceAll("n\'t"," n\'t").replaceAll("\\$"," dollars ").replaceAll("\\%"," percent ").replaceAll("\\&"," and ").replaceAll("\\-","").replaceAll("\\:"," ").replaceAll("\\;"," ").replaceAll("\""," ").replaceAll("\\!"," ").replaceAll("\t"," ");
		while (phase2.contains("  ")) {
			phase2 = phase2.replaceAll("  ", " ");
		}
		while (phase2.startsWith(" ")) {
			phase2 = phase2.substring(1, phase2.length());
		}
		while (phase2.endsWith(" ")) {
			phase2 = phase2.substring(0, phase2.length()-1);
		}
		final String[] cased = phase2.split(" ");
		for (int index = 0; index < cased.length; index++) {
			cased[index] = cased[index].toLowerCase();
		}
		return cased;
	}
	/**
	 * A static reimplementation of Javascript's Array.join.
	 * @param String[] input An array of Strings to be joined together.
	 * @param String joiner A string to be used as the separating phrase. 
	 * @return String result A single string of all of the members of input in order, separated by instances of joiner.
	 */
	public static final String join(final String[] input, final String joiner) {
		if (input==null) {
			return "";
		}
		if (input.length==0) {
			return "";
		} 
		if (joiner==null) {
			return join(input, "");
		}
		final SpeedConcat sc = (new SpeedConcat()).append(input[0]);
		for (int index = 1; index < input.length; index++) {
			sc.append(joiner + input[index]);
		}
		return sc.concat();
	}
}

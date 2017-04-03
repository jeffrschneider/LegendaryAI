package ai.legendary.tuple;
import java.sql.*;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;

import ai.legendary.core.WordTupleConnection;
public class QueryKit {
	private WordTupleConnection database = null;
	/**
	 * This is what is yielded if a ngram function doesn't find anything.
	 */
	public static final BigDecimal NONE = WordTupleConnection.NONE;
	/**
	 * This takes a WordTupleConnection object and yields a QueryKit object. QueryKit objects are sets of prebuilt procedures that do things with a WordTupleConneciton object.
	 * @param WordTupleConnection db The connection that all of the prebuilt functions will use.
	 * @return QueryKit the QueryKit in question. It's a constructor. 
	 */
	public QueryKit(final WordTupleConnection db) {
		database = db;
	}
	private QueryKit() throws Exception {
		throw new Exception("This should never be seen. QueryKit.QueryKit()");
	}
	/**
	 * Takes a set of 2 words, and returns a BigDecimal which contains how often this combination of words is, compared to all combinations of words in the database.
	 * @param String Word1 It's word 1 in the set.
	 * @param String Word2 It's word 2 in the set.
	 * @return BigDecmial The likelihood of the combination of words compared to all combinations.
	 * @exception An exception which may be thrown if there's an issue executing the query, or performing secondary processing on it.
	 */
	public final BigDecimal ngram2Pct(final String Word1, final String Word2) throws SQLException {
		final ResultSet r = database.query("SELECT `Percent` FROM `2Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\'\'") + "');");
		return PercentJob(r);
	}
	/**
	 * Takes a set of 3 words, and returns a BigDecimal which contains how often this combination of words is, compared to all combinations of words in the database.
	 * @param String Word1 It's word 1 in the set.
	 * @param String Word2 It's word 2 in the set.
	 * @param String Word3 It's word 3 in the set.
	 * @return BigDecmial The likelihood of the combination of words compared to all combinations.
	 * @exception An exception which may be thrown if there's an issue executing the query, or performing secondary processing on it.
	 */
	public final BigDecimal ngram3Pct(final String Word1, final String Word2, final String Word3) throws SQLException {
		final ResultSet r = database.query("SELECT `Percent` FROM `3Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\'\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\'\'") + "');");
		return PercentJob(r);
	}
	/**
	 * Takes a set of 4 words, and returns a BigDecimal which contains how often this combination of words is, compared to all combinations of words in the database.
	 * @param String Word1 It's word 1 in the set.
	 * @param String Word2 It's word 2 in the set.
	 * @param String Word3 It's word 3 in the set.
	 * @param String Word4 It's word 4 in the set.
	 * @return BigDecmial The likelihood of the combination of words compared to all combinations.
	 * @exception An exception which may be thrown if there's an issue executing the query, or performing secondary processing on it.
	 */
	public final BigDecimal ngram4Pct(final String Word1, final String Word2, final String Word3, final String Word4) throws SQLException {
		final ResultSet r = database.query("SELECT `Percent` FROM `4Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\'\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\'\'") + "' AND `Word4`='" + Word4.replaceAll("\'", "\'\'") + "');");
		return PercentJob(r);
	}
	/**
	 * Takes a set of 5 words, and returns a BigDecimal which contains how often this combination of words is, compared to all combinations of words in the database.
	 * @param String Word1 It's word 1 in the set.
	 * @param String Word2 It's word 2 in the set.
	 * @param String Word3 It's word 3 in the set.
	 * @param String Word4 It's word 4 in the set.
	 * @param String Word5 It's word 5 in the set.
	 * @return BigDecmial The likelihood of the combination of words compared to all combinations.
	 * @exception An exception which may be thrown if there's an issue executing the query, or performing secondary processing on it.
	 */
	public final BigDecimal ngram5Pct(final String Word1, final String Word2, final String Word3, final String Word4, final String Word5) throws SQLException {
		final ResultSet r = database.query("SELECT `Percent` FROM `5Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\'\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\'\'") + "' AND `Word4`='" + Word4.replaceAll("\'", "\'\'") + "' AND `Word5`='" + Word5.replaceAll("\'", "\'\'") + "');");
		return PercentJob(r);
	}
	private static final BigDecimal PercentJob(final ResultSet r) throws SQLException {
		if (r.first()) {
			return r.getBigDecimal("Percent");
		}
		return NONE;
	}
	private static final String[] ToColumnList(final ResultSet r, final String col) throws SQLException {
		if (r.first()) {
			final LinkedList<String> result = new LinkedList<String>();
			result.add(r.getString(col));
			while (!r.isLast()) {
				r.next();
				result.add(r.getString(col));
			}
			return result.toArray(new String[result.size()]);
		} 
		return null;
	}
	/**
	 * Takes a set of 4 words, and returns an array of strings which are the words that are known to follow, sorted by how likely they are to follow said set of words, in descending order.
	 * @param String Word1 It's word1 of the starting set.
	 * @param String Word2 It's word2 of the starting set.
	 * @param String Word3 It's word3 of the starting set.
	 * @param String Word4 It's word4 of the starting set.
	 * @return String[] The possible following words, in order from most to least likely. If there's no possibilities, then it's just a NULL value.
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getFinalWordFromNGram5 (final String Word1, final String Word2, final String Word3, final String Word4) throws SQLException {
		final ResultSet r = database.query("SELECT `Word5` FROM `5Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\'\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\'\'") + "' AND `Word4`='" + Word4.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word5");
	}
	/**
	 * Takes a set of 3 words, and returns an array of strings which are the words that are known to follow, sorted by how likely they are to follow said set of words, in descending order.
	 * @param String Word1 It's word1 of the starting set.
	 * @param String Word2 It's word2 of the starting set.
	 * @param String Word3 It's word3 of the starting set.
	 * @return String[] The possible following words, in order from most to least likely. If there's no possibilities, then it's just a NULL value.
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getFinalWordFromNGram4 (final String Word1, final String Word2, final String Word3) throws SQLException {
		final ResultSet r = database.query("SELECT `Word4` FROM `4Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\'\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word4");
	}
	/**
	 * Takes a set of 2 words, and returns an array of strings which are the words that are known to follow, sorted by how likely they are to follow said set of words, in descending order.
	 * @param String Word1 It's word1 of the starting set.
	 * @param String Word2 It's word2 of the starting set.
	 * @return String[] The possible following words, in order from most to least likely. If there's no possibilities, then it's just a NULL value.
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getFinalWordFromNGram3 (final String Word1, final String Word2) throws SQLException {
		final ResultSet r = database.query("SELECT `Word3` FROM `3Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word3");
	}
	/**
	 * Takes 1 word, and returns an array of strings which are the words that are known to follow, sorted by how likely they are to follow said word, in descending order.
	 * @param String Word1 It's preceding word.
	 * @return String[] The possible following words, in order from most to least likely. If there's no possibilities, then it's just a NULL value.
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getFinalWordFromNGram2 (final String Word1) throws SQLException {
		final ResultSet r = database.query("SELECT `Word2` FROM `2Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word2");
	}
	/**
	 * Takes a sentence in the form of an array of strings of words, and returns an array of integers which are objectifications of integers which are indexes of word-choice errors.
	 * @param String[] an array of words in String format.
	 * @return Integer[] an array of Integer Objects which are indexes of vocabulary failures. Ideally, these would be ints, but Java is asinine. 
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final Integer[] getFailurePoints (final String[] Sentence) throws Exception {
		final LinkedList<Integer> result = new LinkedList<Integer>();
		for (int upperIndex = 1; upperIndex < Sentence.length; upperIndex++) {
			final BigDecimal compareJob = ngram2Pct(Sentence[upperIndex-1], Sentence[upperIndex]);
			if (compareJob.equals(NONE)) {
				result.add(new Integer(upperIndex));
			}
		}
		return result.toArray(new Integer[result.size()]);
	}
	/**
	 * Takes 4 words in order in String format in order, and returns the words that are likely to precede them in descending likelihood. The result is in array-of-Strings format.
	 * @param String Word2 Word #2 in the sequence.
	 * @param String Word3 Word #3 in the sequence.
	 * @param String Word4 Word #4 in the sequence.
	 * @param String Word5 Word #5 in the sequence.
	 * @return String[] suggestions Possibilities which are only correct from the perspective of previous data. 
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getFirstWordFromNGram5 (final String Word2, final String Word3, final String Word4, final String Word5) throws SQLException {
		final ResultSet r = database.query("SELECT `Word1` FROM `5Grams` WHERE (`Word2`='" + Word2.replaceAll("\'", "\'\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\'\'") + "' AND `Word4`='" + Word4.replaceAll("\'", "\'\'") + "' AND `Word5`='" + Word5.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word1");
	}
	/**
	 * Takes 3 words in order in String format in order, and returns the words that are likely to precede them in descending likelihood. The result is in array-of-Strings format.
	 * @param String Word2 Word #2 in the sequence.
	 * @param String Word3 Word #3 in the sequence.
	 * @param String Word4 Word #4 in the sequence.
	 * @return String[] suggestions Possibilities which are only correct from the perspective of previous data. 
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getFirstWordFromNGram4 (final String Word2, final String Word3, final String Word4) throws SQLException {
		final ResultSet r = database.query("SELECT `Word1` FROM `4Grams` WHERE (`Word2`='" + Word2.replaceAll("\'", "\'\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\'\'") + "' AND `Word4`='" + Word4.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word1");
	}
	/**
	 * Takes 2 words in order in String format in order, and returns the words that are likely to precede them in descending likelihood. The result is in array-of-Strings format.
	 * @param String Word2 Word #2 in the sequence.
	 * @param String Word3 Word #3 in the sequence.
	 * @return String[] suggestions Possibilities which are only correct from the perspective of previous data. 
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getFirstWordFromNGram3 (final String Word2, final String Word3) throws SQLException {
		final ResultSet r = database.query("SELECT `Word1` FROM `3Grams` WHERE (`Word2`='" + Word2.replaceAll("\'", "\'\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word1");
	}
	/**
	 * Takes 1 word in String format, and returns the words that are likely to precede it in descending likelihood. The result is in array-of-Strings format.
	 * @param String Word2 Word #2 in the sequence.
	 * @return String[] suggestions Possibilities which are only correct from the perspective of previous data. 
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getFirstWordFromNGram2 (final String Word2) throws SQLException {
		final ResultSet r = database.query("SELECT `Word1` FROM `2Grams` WHERE (`Word2`='" + Word2.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word1");
	}
	/**
	 * Takes two preceding words and two following words in order in String format, and returns all of the words which are known to possibly be in the middle, in descending likelihood of possibility, in String format.
	 * @param String Word1 Word #1 in the sequence.
	 * @param String Word2 Word #2 in the sequence.
	 * @param String Word4 Word #4 in the sequence.
	 * @param String Word5 Word #5 in the sequence.
	 * @return String[] suggestions Possibilities which are only correct from the perspective of previous data. 
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getMiddleWordFromNGram5 (final String Word1, final String Word2, final String Word4, final String Word5) throws SQLException {
		final ResultSet r = database.query("SELECT `Word3` FROM `5Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\'\'") + "' AND `Word4`='" + Word4.replaceAll("\'", "\'\'") + "' AND `Word5`='" + Word5.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word3");
	}
	/**
	 * Takes one preceding word and one following word in order in String format, and returns all of the words which are known to possibly be in the middle, in descending likelihood of possibility, in String format.
	 * @param String Word1 Word #1 in the sequence.
	 * @param String Word3 Word #3 in the sequence.
	 * @return String[] suggestions Possibilities which are only correct from the perspective of previous data. 
	 * @exception Exception This code may fail if there's a connection issue, or a postprocessing issue.
	 */
	public final String[] getMiddleWordFromNGram3 (final String Word1, final String Word3) throws SQLException {
		final ResultSet r = database.query("SELECT `Word2` FROM `3Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\'\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\'\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word2");
	}
}

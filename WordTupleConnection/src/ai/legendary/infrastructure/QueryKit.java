package ai.legendary.infrastructure;
import java.sql.*;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
public class QueryKit {
	WordTupleConnection database = null;
	public static final BigDecimal NONE = WordTupleConnection.NONE;
	public QueryKit(final WordTupleConnection db) {
		database = db;
	}
	private QueryKit() throws Exception {
		throw new Exception("This should never be seen.");
	}
	public final BigDecimal ngram2Pct(final String Word1, final String Word2) throws SQLException {
		final ResultSet r = database.query("SELECT `Percent` FROM `2Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\\\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\\\'") + "');");
		return PercentJob(r);
	}
	public final BigDecimal ngram3Pct(final String Word1, final String Word2, final String Word3) throws SQLException {
		final ResultSet r = database.query("SELECT `Percent` FROM `2Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\\\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\\\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\\\'") + "');");
		return PercentJob(r);
	}
	public final BigDecimal ngram4Pct(final String Word1, final String Word2, final String Word3, final String Word4) throws SQLException {
		final ResultSet r = database.query("SELECT `Percent` FROM `2Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\\\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\\\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\\\'") + "' AND `Word4`='" + Word4.replaceAll("\'", "\\\'") + "');");
		return PercentJob(r);
	}
	public final BigDecimal ngram5Pct(final String Word1, final String Word2, final String Word3, final String Word4, final String Word5) throws SQLException {
		final ResultSet r = database.query("SELECT `Percent` FROM `2Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\\\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\\\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\\\'") + "' AND `Word4`='" + Word4.replaceAll("\'", "\\\'") + "' AND `Word5`='" + Word5.replaceAll("\'", "\\\'") + "');");
		return PercentJob(r);
	}
	private static final BigDecimal PercentJob(final ResultSet r) throws SQLException {
		if (r.first()) {
			return r.getBigDecimal("Percent");
		} else {
			return NONE;
		}
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
		} else {
			return null;
		}
	}
	public final String[] getFinalWordFromNGram5 (final String Word1, final String Word2, final String Word3, final String Word4) throws SQLException {
		final ResultSet r = database.query("SELECT `Word5` FROM `5Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\\\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\\\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\\\'") + "' AND `Word4`='" + Word4.replaceAll("\'", "\\\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word5");
	}
	public final String[] getFinalWordFromNGram4 (final String Word1, final String Word2, final String Word3) throws SQLException {
		final ResultSet r = database.query("SELECT `Word4` FROM `4Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\\\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\\\'") + "' AND `Word3`='" + Word3.replaceAll("\'", "\\\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word4");
	}
	public final String[] getFinalWordFromNGram3 (final String Word1, final String Word2) throws SQLException {
		final ResultSet r = database.query("SELECT `Word3` FROM `3Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\\\'") + "' AND `Word2`='" + Word2.replaceAll("\'", "\\\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word3");
	}
	public final String[] getFinalWordFromNGram2 (final String Word1) throws SQLException {
		final ResultSet r = database.query("SELECT `Word2` FROM `2Grams` WHERE (`Word1`='" + Word1.replaceAll("\'", "\\\'") + "') ORDER BY `Percent` DESC;");
		return ToColumnList(r, "Word2");
	}
}

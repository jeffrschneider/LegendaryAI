package ai.legendary.tuple;

import java.io.PrintWriter;
import java.math.BigDecimal;

import ai.legendary.core.SpeedConcat;

public class NGramObject {
	private String[] tokens = null;
	private BigDecimal percentage = null;
	private static final BigDecimal zero = new BigDecimal("0.0");
	private static final void nullCheck(final Object o) throws NullPointerException{
		if (o==null) {
			throw new NullPointerException();
		}
	}
	public NGramObject() {
		
	}
	public final BigDecimal getPercentage(){
		return percentage;
	}
	public final String[] getTokens(){
		return tokens;
	}
	public final NGramObject setTokens(final String[] input) {
		tokens = input;
		return this;
	}
	public final NGramObject setPercentage(final BigDecimal input) {
		if (input==null) {
			return setPercentage(zero);
		}
		percentage = input;
		return this;
	}
	public final NGramObject print (final PrintWriter printer) throws Exception {
		SpeedConcat.print(export(), printer);
		return this;
	}
	public final String toString() {
		if (tokens==null) {
			setTokens(new String[0]);
		}
		if (percentage==null) {
			setPercentage(zero);
		}
		return export().concat();
	}
	public final SpeedConcat export () throws NullPointerException{
		nullCheck(tokens);
		nullCheck(percentage);
		final SpeedConcat result = (new SpeedConcat()).append("{ \"tokens\":[");
		if (tokens.length > 0) {
			result.append("\"").append(tokens[0]).append("\"");
		}
		for (int index = 1; index < tokens.length ; index++) {
			result.append(", \"").append(tokens[index].replaceAll("\"", "\\\"").replaceAll("\'", "\\\'")).append("\"");
		}
		result.append("], \"percentage\": \"");
		result.append(percentage);
		result.append("\"}");
		return result;
	}
}

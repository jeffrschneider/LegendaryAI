package ai.legendary.infrastructure;

import java.util.Arrays;

import ai.legendary.core.stringUtils;
import opennlp.tools.util.Span;

@SuppressWarnings("rawtypes")
public class Identifier implements Comparable{
	private String[] nameparts = null;
	private String type = "noun";
	public Identifier(final String[] name) throws Exception {
		if (name == null) {
			throw new Exception();
		}
		if (name.length == 0) {
			throw new Exception();
		}
		nameparts = name;
		return;
	}
	public int compareTo(final Object o) {
		if (o instanceof Identifier) {
			return (
					this.JSONexport().compareTo(((Identifier) o).JSONexport())
				);
		}
		return (
				this.JSONexport().compareTo(o.toString())
				);
	}
	public String toString() {
		return stringUtils.join(nameparts, " ");
	}
	public final Identifier setType(final String input) throws Exception {
		if (input==null) {
			throw new Exception();
		}
		type = input;
		return this;
	}
	public final String getType() {
		return type;
	}
	public final String JSONexport() {
		return (("{\"type\":\"" + type) + "\", \"words\": \"") + (stringUtils.join(nameparts, " ") + "\"}");
	}
	public final static Identifier MakeIdentifier(final Span input, final String[] sentence) throws Exception {
		final Identifier result = new Identifier(Arrays.copyOfRange(sentence, input.getStart(), input.getEnd()));
		return result.setType(input.getType());
	}
	public boolean equals(final Identifier input) {
		return (
				(this.getType().equals(input.getType()))
			&&
				(this.toString().equals(input.toString()))
		);
	}
}

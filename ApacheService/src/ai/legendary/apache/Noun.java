package ai.legendary.apache;

import ai.legendary.core.stringUtils;

public class Noun implements Comparable{
	private String name = null;
	private String category = "noun";
	public Noun() {
		return;
	}
	public Noun(final String inname) throws Exception {
		setName(inname);
	}
	public Noun(final String[] inname) throws Exception {
		setName(inname);
	}
	public Noun(final String inname, final String runway) throws Exception {
		setName(inname);
		setCategory(runway);
	}
	public Noun(final String[] inname, final String runway) throws Exception {
		setName(inname);
		setCategory(runway);
	}
	public final String getName() {
		return name;
	}
	public final String getCategory() {
		return category;
	}
	public final Noun setName(final String in) throws Exception {
		if (in==null) {
			throw new Exception();
		}
		name = in;
		return this;
	}
	public final Noun setName(final String[] in) throws Exception {
		if (in==null) {
			throw new Exception();
		}
		if (in.length==0) {
			throw new Exception();
		}
		name = stringUtils.join(in, " ");
		return this;
	}
	public final Noun setCategory(final String in) throws Exception {
		if (in==null) {
			throw new Exception();
		}
		category = in;
		return this;
	}
	public final String JSONexport() {
		return (("{\"name\": \"" + name) + (("\", \"category\": \"" + category) + "\"}"));
	}
	public final String toString(){
		return this.JSONexport();
	}
	public final boolean equals(final Noun input) {
		return (this.getName().equals(input.getName())) && (this.getCategory().equals(input.getCategory()));
	}
	public final int compareTo(final Object arg0) {
		if (arg0 instanceof Noun) {
			final Noun arg1 = (Noun) arg0;
			return this.JSONexport().compareTo(arg1.JSONexport());
		}
		return this.JSONexport().compareTo(arg0.toString());
	}
	
}

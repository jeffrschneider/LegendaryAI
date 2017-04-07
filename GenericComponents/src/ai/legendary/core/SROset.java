package ai.legendary.core;

public class SROset {
	private static final void nullCheck(final Object o) {
		if (o==null) {
			throw new NullPointerException();
		}
		return;
	}
	private String subject = "";
	private String relationship = "";
	private String object = "";
	public SROset(){
		return;
	}
	public final SROset setSubject(final String input) {
		nullCheck(input);
		subject = input;
		return this;
	}
	public final SROset setRelationship(final String input) {
		nullCheck(input);
		relationship = input;
		return this;
	}
	public final SROset setObject(final String input) {
		nullCheck(input);
		object = input;
		return this;
	}
	public final String getSubject(){
		return subject;
	}
	public final String getRelationship(){
		return relationship;
	}
	public final String getObject(){
		return object;
	}
	public static final String toJSON(final SROset input) {
			return (new SpeedConcat())
				.append("{\"subject\": \"")
				.append(input.subject.replaceAll("\"", "\\\"").replaceAll("\'", "\\\'"))
				.append("\", \"relationship\": \"")
				.append(input.relationship.replaceAll("\"", "\\\"").replaceAll("\'", "\\\'"))
				.append("\", \"object\": \"")
				.append(input.object.replaceAll("\"", "\\\"").replaceAll("\'", "\\\'"))
				.append("\"}")
				.concat();
	}
}

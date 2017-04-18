package ai.legendary.core;

import java.io.PrintWriter;

public class InformationExtractorResult {
	private String context = "";
	private String subject = "";
	private String relation = "";
	private String object = "";
	private static final void nullCheck (final Object o) {
		if (o==null) {
			throw new NullPointerException();
		}
		return;
	}
	public InformationExtractorResult(){
		
	}
	public final InformationExtractorResult setContext(final String input) {
		nullCheck(input);
		context = input;
		return this;
	}
	public final InformationExtractorResult setSubject(final String input) {
		nullCheck(input);
		subject = input;
		return this;
	}
	public final InformationExtractorResult setRelation(final String input) {
		nullCheck(input);
		relation = input;
		return this;
	}
	public final InformationExtractorResult setObject(final String input) {
		nullCheck(input);
		object = input;
		return this;
	}
	public final String getContext (){
		return context;
	}
	public final String getSubject (){
		return subject;
	}
	public final String getRelation (){
		return relation;
	}
	public final String getObject (){
		return object;
	}
	public static final PrintWriter printAsArray(final InformationExtractorResult input, final PrintWriter writer) {
		writer.write("[\"");
		writer.write(input.context.replaceAll("\"", "\\\""));
		writer.write("\",\"");
		writer.write(input.subject.replaceAll("\"", "\\\""));
		writer.write("\",\"");
		writer.write(input.relation.replaceAll("\"", "\\\""));
		writer.write("\",\"");
		writer.write(input.object.replaceAll("\"", "\\\""));
		writer.write("\"]");
		return writer;
	}
	public static final PrintWriter printAsObject(final InformationExtractorResult input, final PrintWriter writer) {
		writer.write("{\"context\":\"");
		writer.write(input.context.replaceAll("\"", "\\\""));
		writer.write("\",\"subject\":\"");
		writer.write(input.subject.replaceAll("\"", "\\\""));
		writer.write("\",\"relation\":\"");
		writer.write(input.relation.replaceAll("\"", "\\\""));
		writer.write("\",\"object\":\"");
		writer.write(input.object.replaceAll("\"", "\\\""));
		writer.write("\"}");
		return writer;
	}
}

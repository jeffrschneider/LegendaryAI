package ai.legendary.testscripts;

import ai.legendary.infrastructure.ApacheNameIdentifier;
import ai.legendary.infrastructure.CombinationApacheNameIdentifier;
import ai.legendary.infrastructure.Identifier;

public class ApacheNameIdentifierTester {

	public static final void main(String[] args) throws Exception {
		final String path = "C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\WordTupleConnection\\WebContent\\WEB-INF\\";
		final ApacheNameIdentifier ani = new ApacheNameIdentifier(path + "en-ner-person.bin", false);
		final String[] tokens = new String[]{"Ben", "wrote", "a", "sentence", "."};
		final Identifier[] identities = ani.getNames(tokens);
		for (final Identifier i: identities) {
			System.out.println(i.getType());
			System.out.println(i.toString());
		}
		System.out.println(ApacheNameIdentifier.JSONbuild(null, identities));
		bulkJob();
		return;
	}
	private static final void bulkJob () throws Exception {
		final String path = "C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\WordTupleConnection\\WebContent\\WEB-INF\\";
		final String[] filenames = new String[]{ "en-ner-date.bin", "en-ner-location.bin", "en-ner-money.bin",
				"en-ner-organization.bin", "en-ner-percentage.bin", "en-ner-person.bin", "en-ner-time.bin",
				"es-ner-location.bin", "es-ner-misc.bin", "es-ner-organization.bin", "es-ner-person.bin",
				"nl-ner-misc.bin", "nl-ner-organization.bin", "nl-ner-person.bin"};
		final String[] files = new String[filenames.length];
		for (int index = 0; index < filenames.length; index++) {
			files[index] = path + filenames[index];
		}
		final CombinationApacheNameIdentifier cani = new CombinationApacheNameIdentifier(files);
		final String[] tokens = new String[]{"Ben", "wrote", "a", "sentence", "."};
		final Identifier[] identities = cani.getNames(tokens);
		for (final Identifier i: identities) {
			System.out.println(i.getType());
			System.out.println(i.toString());
		}
		System.out.println(CombinationApacheNameIdentifier.JSONbuild(null, identities));
		return;
	}
}

package ai.legendary.genderAPI;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import ai.legendary.core.WordTupleConnection;
import ai.legendary.core.stringUtils;
import ai.legendary.genderAPI.GenderService.genderData;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class GenderService {
	private WordTupleConnection wtc = null;
	public GenderService(final WordTupleConnection input) {
		wtc = input;
		final Properties props = new Properties();
		props.put("annotators", "tokenize,ssplit,pos,lemma,ner");
		pipeline = new StanfordCoreNLP(props);
	}
	private StanfordCoreNLP pipeline = null;
	public static enum genderData {
		male, female, neutral, organization, indeterminate
	}
	private static void langHandle(final String lang) throws Exception {
		nullCheck(lang);
		switch (lang) {
			case "eng":
				break;
			default:
				throw new Exception("language unsupported");
		}
		return;
	}
	private static void nullCheck(final Object o) throws NullPointerException {
		if (o==null) {
			throw new NullPointerException();
		}
	}
	public final LinkedList<CoreLabel> tokenize(final String input, final String lang) throws Exception {
		langHandle(lang);
		final Annotation doc = new Annotation(input);
		pipeline.annotate(doc);
		final LinkedList<CoreLabel> results = new LinkedList<CoreLabel>();
	    final List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for (final CoreMap sentence: sentences) {
			for (final CoreLabel token: sentence.get(TokensAnnotation.class)) {
				results.add(token);
			}
		}
		return results;
	}
	public final LinkedList<String[]> findNames(final String input, final String lang) throws Exception {
		langHandle(lang);
		final Annotation doc = new Annotation(input);
		pipeline.annotate(doc);
		final LinkedList<String[]> results = new LinkedList<String[]>();
	    final List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for (final CoreMap sentence: sentences) {
			boolean prevName = false;
			for (final CoreLabel token: sentence.get(TokensAnnotation.class)) {
				final String name = token.get(NamedEntityTagAnnotation.class);
				if (name != null) {
					if (!(name.equals("O"))) {
						if (prevName==false) {
							results.add(token.get(TextAnnotation.class).split(" "));
							prevName = true;
						} else {
							final String replacementName = stringUtils.join(results.getLast(), " ") + " " + token.get(TextAnnotation.class);
							results.removeLast();
							results.addLast(replacementName.split(" "));
							prevName = true;
						}
					} else {
						prevName = false;
					}
				}
			}
		}
		return results;
	}
	public LinkedList<NounObject> generateNounObjects (final String input, final String lang) throws Exception {
		nullCheck(input);
		langHandle(lang);
		final LinkedList<NounObject> result = new LinkedList<NounObject>();
		final LinkedList<String[]> names = findNames(input, lang);
		for (int index = 0; index < names.size(); index++) {
			final NounObject no = new NounObject();
			no.nameParts = names.get(index);
			if (no.nameParts==null) {
				throw new Exception();
			}
			no.genders = comboName(no.nameParts, lang);
			result.add(no);
		}
		return result;
	}
	public static final String genderExport(final genderData input) throws Exception {
		nullCheck(input);
		switch (input) {
			case female:
				return "female";
			case indeterminate:
				return "indeterminate";
			case male:
				return "male";
			case neutral:
				return "neutral";
			case organization:
				return "organization";
			default:
				throw new Exception();
		}
	}
	public final genderData[] comboName(final String[] input, final String lang) throws Exception {
		final genderData[] prereduce = new genderData[input.length];
		for (int index = 0; index < input.length; index++) {
			prereduce[index] = compositeCheck(input[index], lang);
		}
		final genderData coreResult = reduceGenderData(prereduce);
		final genderData[] result = new genderData[prereduce.length];
		for (int index = 0; index < result.length; index++) {
			result[index] = coreResult;
		}
		return result;
	}
	public final genderData compositeCheck(final String input, final String lang) throws Exception { 
		nullCheck(input);
		langHandle(lang);
		return reduceGenderData(singleCheck(input, lang));
	}
	public static final genderData reduceGenderData(final genderData[] padded) throws Exception {
		nullCheck(padded);
		if (genderIndex(padded, genderData.organization) > -1) {
			return genderData.organization;
		}
		if (genderIndex(padded, genderData.male) > -1) {
			if (genderIndex(padded, genderData.female) == -1) {
				return genderData.male;
			}
			return genderData.neutral;
		}
		if (genderIndex(padded, genderData.female) > -1) {
			if (genderIndex(padded, genderData.male) == -1) {
				return genderData.female;
			}
			return genderData.neutral;
		}
		if (genderIndex(padded, genderData.neutral) > -1) {
			return genderData.neutral;
		}
		return genderData.indeterminate;
	}
	public static final int genderIndex(final genderData[] padded, final genderData target) throws Exception  {
		nullCheck(padded);
		nullCheck(target);
		for (int index = 0; index < padded.length; index++) {
			if (padded[index].equals(target)) {
				return index;
			}
		}
		return -1;
	}
	public final genderData[] singleCheck(final String input, final String lang) throws Exception {
		nullCheck(input);
		langHandle(lang);
		final String name = input.replaceAll("\'", "\\\'");
		final String language = lang.replaceAll("\'", "\\\'");
		final ResultSet data = wtc.query("SELECT `Gender` FROM `genders` WHERE (`Name`='" + name + "' AND (`Language`='" + language + "'));");
		final LinkedList<genderData> result = new LinkedList<genderData>();
		if (data.first()) {
			result.add(StringToGender(data.getString("Gender")));
			while (!data.isLast()) {
				data.next();
				result.add(StringToGender(data.getString("Gender")));
			}
			return result.toArray(new genderData[result.size()]);
		} 
		return new genderData[0];
	}
	private static final genderData StringToGender(final String string) throws Exception {
		switch (string) {
			case "m":
				return genderData.male;
			case "f":
				return genderData.female;
			case "n":
				return genderData.neutral;
			case "o":
				return genderData.organization;
			case "i":
				return genderData.indeterminate;
			default:
				throw new Exception();
		}
	}
	public void populateGenders(final LinkedList<CoreLabel> tokens, final genderData[] genders, final String[] tokenSet, final String lang) throws Exception {
		langHandle(lang);
		final boolean[] named = new boolean[genders.length];
		for (int index = 0; index < tokens.size(); index++) {
			final CoreLabel token = tokens.get(index);
			tokenSet[index] = token.get(TextAnnotation.class);
			final boolean AmName = !(token.get(NamedEntityTagAnnotation.class).equals("O"));
			if (AmName) {
				final genderData[] firstChecks = singleCheck(token.get(TextAnnotation.class), lang);
				final genderData reduced = reduceGenderData(firstChecks);
				genderSwitch:
				switch (reduced) {
					case male:
					case female:
					case organization:
						genders[index] = reduced;
						break genderSwitch;
					case neutral:
					case indeterminate:
						if (index > 0) {
							if (genders[index-1] != null) {
								genders[index] = genders[index-1];
								break genderSwitch;
							}
						}
						genders[index] = reduced;
						break genderSwitch;
				}
			} else {
				genders[index] = null;
			}
		}
		return;
	}
}

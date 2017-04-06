package ai.legendary.stanford;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.util.CoreMap;

public class StemService {
	private StanfordCoreNLP pipeline = null;
	public StemService() {
		final Properties props = new Properties();
		props.put("annotators", "tokenize,ssplit");//,pos,lemma,ner
		pipeline = new StanfordCoreNLP(props);
	}
	private static final void nullCheck(final Object o) {
		if (o==null) {
			throw new NullPointerException();
		}
	}
	public final String getStem(final String input) {
		nullCheck(input);
		final Morphology m = new Morphology();
        return (m.stem(input));
	}
	public final String[] getStems(final String[] input) {
		nullCheck(input);
		final String[] result = new String[input.length];
		for (int index = 0; index < result.length; index++) {
			result[index] = getStem(input[index]);
		}
		return result;
	}
	public final String[] tokenize (final String input) {
		final Annotation doc = new Annotation(input);
		pipeline.annotate(doc);
		final LinkedList<String> result = new LinkedList<String>();
	    final List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for (final CoreMap sentence: sentences) {
			for (final CoreLabel token: sentence.get(TokensAnnotation.class)) {
				result.add(token.get(TextAnnotation.class));
			}
		}
		return result.toArray(new String[result.size()]);
	}
}

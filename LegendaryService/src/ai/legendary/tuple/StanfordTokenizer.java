package ai.legendary.tuple;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import ai.legendary.core.WordTupleConnection;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordTokenizer {
	
	public StanfordTokenizer() {
		final Properties props = new Properties();
		props.put("annotators", "tokenize,ssplit");
		pipeline = new StanfordCoreNLP(props);
	}
	private StanfordCoreNLP pipeline = null;
	public final String[] tokenize(final String input) throws Exception {
		LinkedList<CoreLabel> tokens = coreLabelTokenize(input);
		final String[] result = new String[tokens.size()];
		int index = 0;
		final Iterator<CoreLabel> i = tokens.iterator();
		while (i.hasNext()) {
			final CoreLabel token = i.next();
			result[index] = token.get(TextAnnotation.class);
			index++;
		}
		return result;
	}
	public final LinkedList<CoreLabel> coreLabelTokenize(final String input) throws Exception {
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
}

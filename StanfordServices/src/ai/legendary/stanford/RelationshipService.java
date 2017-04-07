package ai.legendary.stanford;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import ai.legendary.core.SROset;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;

public class RelationshipService {

	private StanfordCoreNLP pipeline = null;
	public RelationshipService() {
		final Properties props = new Properties();
		props.put("annotators", "tokenize,ssplit");//,pos,lemma,ner
		pipeline = new StanfordCoreNLP(props);
	}
	public LinkedList<SROset> getSROsets(final String input) {
		final Annotation doc = new Annotation(input);
		pipeline.annotate(doc);
		final LinkedList<SROset> result = new LinkedList<SROset>();
	    final List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for (final CoreMap sentence: sentences) {
			final String text = sentence.toString();
			final Sentence sent = new Sentence(text);
	        final Collection<RelationTriple> trips = sent.openieTriples(); 
	        for (final RelationTriple triple : trips) {
	        	result.add(
	        			new SROset()
	        				.setSubject(triple.subjectGloss())
	        				.setRelationship(triple.relationGloss())
	        				.setObject(triple.objectGloss())
	        		);
	        }
		}
		return result;
	}
}

package ai.legendary.secondary;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.util.CoreMap;

public class IETest {

    public IETest() {
    	final String txt = "Bob Smith and Susan Jones ate warm pizza and drank cold coke.";
    	
		final Properties props = new Properties();
		props.put("annotators", "tokenize,ssplit");//,pos,lemma,ner
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		final Annotation doc = new Annotation(txt);
		pipeline.annotate(doc);
	    final List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for (final CoreMap sentence: sentences) {
			final String text = sentence.toString();
	        Sentence sent = new Sentence(text);
	        System.out.println(sent);
	        
	        Collection<RelationTriple> trips = sent.openieTriples(); 
	        for (  RelationTriple triple : trips) {
	            System.out.println( triple.subjectGloss() + ", " + triple.relationGloss() +", "+ triple.objectGloss());
	        }
			
		}
    }
    public static final void main (final String[] args) {
    	final IETest internal = new IETest();
    	return;
    }
}
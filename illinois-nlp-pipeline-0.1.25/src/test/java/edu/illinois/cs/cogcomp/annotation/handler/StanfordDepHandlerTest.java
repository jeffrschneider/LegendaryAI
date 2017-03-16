package edu.illinois.cs.cogcomp.annotation.handler;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotationUtilities;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class StanfordDepHandlerTest {
    private static POSTaggerAnnotator posAnnotator;
    private TextAnnotation ta;
    private static ParserAnnotator parseAnnotator;
    private TextAnnotation shortTa;


    @BeforeClass
    public static void setUpOnce()
    {
        ResourceManager rm = new PipelineConfigurator().getDefaultConfig();
        Properties stanfordProps = new Properties();
        stanfordProps.put( "annotators", "pos, parse") ;
        stanfordProps.put("parse.originalDependencies", true);
        String maxParseSentenceLength = rm.getString( PipelineConfigurator.STFRD_MAX_SENTENCE_LENGTH.key );
        stanfordProps.put( "parse.maxlen", maxParseSentenceLength );
        stanfordProps.put( "parse.maxtime", rm.getInt( PipelineConfigurator.STFRD_TIME_PER_SENTENCE.key ) ); // per sentence? could be per document but no idea from stanford javadoc
        posAnnotator = new POSTaggerAnnotator( "pos", stanfordProps );
        parseAnnotator = new ParserAnnotator( "parse", stanfordProps );
    }

    @Before
    public void setUp() {
        String bigSent = "You all know what Strats sound like and what 5-position selectors do , " +
                "but to get an overall picture of this guitar I lined it up against a regular office hack " +
                "( a Tokai hybrid with an old ‘ 58 Fender neck and Alnico Pro II 's in the middle and bridge " +
                "positions ) and can report that the SRV came out well .";

        ta = TextAnnotationUtilities.createFromTokenizedString(bigSent);

    }

    @Test
    public void testDepParserSizeFail() throws Exception {
        boolean failOnSentenceLengthCheckFailure = true;
        StanfordDepHandler depParser = new StanfordDepHandler(posAnnotator, parseAnnotator, 60, failOnSentenceLengthCheckFailure);

        try {
            depParser.getView(ta);
        } catch (AnnotatorException e) {
            assertTrue(e.getMessage().contains("maximum sentence length"));
        }
        assertFalse(ta.hasView(ViewNames.DEPENDENCY_STANFORD));
    }

    @Test
    public void testDepParserTimeFail() throws Exception {
        Properties stanfordProps = new Properties();
        stanfordProps.put("annotators", "pos, parse");
        stanfordProps.put("parse.originalDependencies", true);
        stanfordProps.put("parse.maxlen",  "100");
        stanfordProps.put("parse.maxtime", "100");
        POSTaggerAnnotator pos = new POSTaggerAnnotator("pos", stanfordProps);
        ParserAnnotator parse = new ParserAnnotator("parse", stanfordProps);
        boolean failOnSentenceLengthCheckFailure = true;
        StanfordDepHandler depParser = new StanfordDepHandler(pos, parse, 100, failOnSentenceLengthCheckFailure);

        try {
            depParser.getView(ta);
        } catch (AnnotatorException e) {
            assertTrue(e.getMessage().contains("timeout"));
        }
        assertFalse(ta.hasView(ViewNames.DEPENDENCY_STANFORD));
    }

    @Test
    public void testDepParserSuccess() throws Exception {
        boolean failOnSentenceLengthCheckFailure = true;
        StanfordDepHandler depParser = new StanfordDepHandler(posAnnotator, parseAnnotator, 100, failOnSentenceLengthCheckFailure);

        try {
//            TextAnnotation ta = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(depParser.getRequiredViews(), false);
            View depView = depParser.getView(ta);
        } catch (AnnotatorException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(ta.hasView(ViewNames.DEPENDENCY_STANFORD));
    }

//    @AfterClass
//    public static void cleanUp()
//    {
//        posAnnotator = null;
//        parseAnnotator = null;
//    }
}
package edu.illinois.cs.cogcomp.nlp.pipeline;

import edu.illinois.cs.cogcomp.annotation.handler.StanfordDepHandler;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.ner.NERAnnotator;
import edu.illinois.cs.cogcomp.ner.NerAnnotatorManager;
import edu.illinois.cs.cogcomp.ner.config.NerBaseConfigurator;
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;

import java.util.Properties;

/**
 * Created by mssammon on 8/13/16.
 */
public class PipelineNonJUnitMemoryTest
{

    private static StanfordDepHandler depStatic;
    private ResourceManager rm;

    public static void main(String[] args) {

        PipelineNonJUnitMemoryTest test = new PipelineNonJUnitMemoryTest();

        try {
            test.setUp();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        System.out.println( "Test method-scope dependency parser:" );

        boolean isStatic = false;
        for ( int num = 0; num < 5; num++ ) {
            System.out.println( "Memory usage before running method-scoped dep: " );
            showMemoryUsage();

            try {
                test.testDep(isStatic);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }

            System.out.println("Finished test #" + num + ".");
        }


        System.out.println( "Running test again, this time with static field." );

        isStatic = true;

        for ( int num = 0; num < 10; num++ ) {

            depStatic = null;
            System.out.println("setting static field to null, and rerunning #" + num + "... memory usage before run:");
            showMemoryUsage();


            try {
                test.testDep(isStatic);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }

        }


    }

    public void setUp() throws Exception {
        rm = new PipelineConfigurator().getDefaultConfig();
    }

    public void testDep(boolean isStatic) throws Exception {

        StanfordDepHandler dep = getDep(isStatic);
        TextAnnotation ta = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(dep.getRequiredViews(), false, 3);
        View nerView = dep.getView(ta);

        System.out.println( "Dep output: " + nerView.toString() );
        System.out.println( "memory use while dep instantiated " + (isStatic ?  "before dep goes out of scope" : "") +":" );
        showMemoryUsage();
    }

    private StanfordDepHandler getDep(boolean isStatic) throws Exception {
//        Properties props = new Properties();

//        rm = Configurator.mergeProperties(rm, new ResourceManager(props));

        StanfordDepHandler dep = null;

        if ( !isStatic ||  null == depStatic )
        {
            Properties stanfordProps = new Properties();
            stanfordProps.put( "annotators", "pos, parse") ;
            stanfordProps.put("parse.originalDependencies", true);
            String maxParseSentenceLength = rm.getString( PipelineConfigurator.STFRD_MAX_SENTENCE_LENGTH.key );
            stanfordProps.put( "parse.maxlen", maxParseSentenceLength );
            stanfordProps.put( "parse.maxtime", rm.getInt( PipelineConfigurator.STFRD_TIME_PER_SENTENCE.key ) ); // per sentence? could be per document but no idea from stanford javadoc
            POSTaggerAnnotator posAnnotator = new POSTaggerAnnotator( "pos", stanfordProps );
            ParserAnnotator parseAnnotator = new ParserAnnotator( "parse", stanfordProps );
            int maxLength = Integer.parseInt(maxParseSentenceLength);
            boolean throwExceptionOnSentenceLengthCheck =
                    rm.getBoolean( PipelineConfigurator.THROW_EXCEPTION_ON_FAILED_LENGTH_CHECK.key );
            dep = new StanfordDepHandler( posAnnotator, parseAnnotator, maxLength,
                    throwExceptionOnSentenceLengthCheck );

        }
        if ( isStatic && null == depStatic )
            depStatic = dep;

        return dep;
    }


    public static void showMemoryUsage()
    {
        int mb = 1024*1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }

}

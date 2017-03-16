package edu.illinois.cs.cogcomp.nlp.pipeline;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.annotation.handler.*;
import edu.illinois.cs.cogcomp.chunker.main.ChunkerAnnotator;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.ner.NERAnnotator;
import edu.illinois.cs.cogcomp.ner.NerAnnotatorManager;
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator;
import edu.illinois.cs.cogcomp.nlp.lemmatizer.IllinoisLemmatizer;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.util.SimpleCachingPipeline;
import edu.illinois.cs.cogcomp.pos.POSAnnotator;
import edu.illinois.cs.cogcomp.srl.SemanticRoleLabeler;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.srl.config.SrlConfigurator;
import edu.illinois.cs.cogcomp.srl.core.SRLType;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Builds an AnnotatorService object with all available CCG NLP components.
 *
 * Created by mssammon on 8/31/15.
 */
public class IllinoisPipelineFactory
{

//    private static final String NER_CONLL_CONFIG = "nerConllConfig";
//    private static final String LEMMA_CONFIG = "lemmaConfig";
//    private static final java.lang.String NER_ONTONOTES_CONFIG = "nerOntonotesConfig";

    /**
     * Builds a complete pipeline with all the tools currently available,
     *   though for now, only one of two possible NER models.
     *
     * TODO:
     *    allow a configuration to specify a subset of annotators to use.
     *      probably, a set of ViewName values to 'true'/'false' entries,
     *      which would allow more or less direct use of Annotator-specified
     *      dependencies to activate components.
     *      CachingAnnotatorService will check these dependencies at run-time,
     *      so if you don't provide a prerequisite for some component, you'll know.
     *
     * TODO:
     *    add a "addAnnotator( Annotator )" method that checks supported views and requirements of new
     *      annotator, and if they are compatible, adds the annotator to the pipeline.
     *
     * TODO:
     *    when NER is updated, set it up to allow multiple different NER components with different models
     * @param nonDefaultRm    ResourceManager with properties for config files, caching behavior
     * @return  an AnnotatorService object with a suite of NLP components.
     */
    public static AnnotatorService buildPipeline( ResourceManager nonDefaultRm ) throws IOException, AnnotatorException
    {
        ResourceManager rm = ( new PipelineConfigurator().getConfig( nonDefaultRm ) );
        Map< String, Annotator > annotators = buildAnnotators( rm );
        IllinoisTokenizer tokenizer = new IllinoisTokenizer();
        TextAnnotationBuilder taBuilder = new TokenizerTextAnnotationBuilder( tokenizer );

//
//        Map< String, Boolean > requestedViews = new HashMap<String, Boolean>();
//        for ( String view : annotators.keySet() )
//            requestedViews.put( view, false );

        return new SimpleCachingPipeline( taBuilder, annotators, rm );
    }

    /**
     * Overloaded builder with default configuration.
     * @return an {@link AnnotatorService} object with a suite of NLP components.
     */
    public static AnnotatorService buildPipeline() throws IOException, AnnotatorException
    {
        return new SimpleCachingPipeline(( new PipelineConfigurator().getDefaultConfig() ));
    }


    /**
     * instantiate a set of annotators for use in an AnnotatorService object
     * by default, will use lazy initialization where possible
     * -- change this behavior with the {@link PipelineConfigurator#USE_LAZY_INITIALIZATION} property.
     * @param nonDefaultRm ResourceManager with all non-default values for Annotators
     * @return a Map from annotator view name to annotator
     */
    public static Map<String, Annotator> buildAnnotators(ResourceManager nonDefaultRm) throws IOException {
        ResourceManager rm = new PipelineConfigurator().getConfig(nonDefaultRm);
        String timePerSentence = rm.getString(PipelineConfigurator.STFRD_TIME_PER_SENTENCE );
        String maxParseSentenceLength = rm.getString( PipelineConfigurator.STFRD_MAX_SENTENCE_LENGTH );
        boolean useLazyInitialization =
                rm.getBoolean( PipelineConfigurator.USE_LAZY_INITIALIZATION.key, PipelineConfigurator.TRUE );

        Map< String, Annotator> viewGenerators = new HashMap<>();

        if( rm.getBoolean( PipelineConfigurator.USE_POS ) ) {
            POSAnnotator pos = new POSAnnotator();
            viewGenerators.put( pos.getViewName(), pos );
        }
        if ( rm.getBoolean( PipelineConfigurator.USE_LEMMA ) )
        {
            IllinoisLemmatizer lem = new IllinoisLemmatizer(rm);
            viewGenerators.put(lem.getViewName(), lem);
        }
        if ( rm.getBoolean( PipelineConfigurator.USE_SHALLOW_PARSE )) {

            viewGenerators.put(ViewNames.SHALLOW_PARSE, new ChunkerAnnotator());
        }
        if ( rm.getBoolean( PipelineConfigurator.USE_NER_CONLL ) ) {
            NERAnnotator nerConll = NerAnnotatorManager.buildNerAnnotator( rm, ViewNames.NER_CONLL );
            viewGenerators.put(nerConll.getViewName(), nerConll);
        }
        if ( rm.getBoolean( PipelineConfigurator.USE_NER_ONTONOTES ) ) {
            NERAnnotator nerOntonotes = NerAnnotatorManager.buildNerAnnotator( rm, ViewNames.NER_ONTONOTES );
            viewGenerators.put(nerOntonotes.getViewName(), nerOntonotes);
        }
        if ( rm.getBoolean( PipelineConfigurator.USE_STANFORD_DEP ) ||
                rm.getBoolean( PipelineConfigurator.USE_STANFORD_PARSE ) )
        {
            Properties stanfordProps = new Properties();
            stanfordProps.put( "annotators", "pos, parse") ;
            stanfordProps.put("parse.originalDependencies", true);
            stanfordProps.put( "parse.maxlen", maxParseSentenceLength );
            stanfordProps.put( "parse.maxtime", timePerSentence ); // per sentence? could be per document but no idea from stanford javadoc
            POSTaggerAnnotator posAnnotator = new POSTaggerAnnotator( "pos", stanfordProps );
            ParserAnnotator parseAnnotator = new ParserAnnotator( "parse", stanfordProps );
            int maxLength = Integer.parseInt(maxParseSentenceLength);
            boolean throwExceptionOnSentenceLengthCheck =
                    rm.getBoolean( PipelineConfigurator.THROW_EXCEPTION_ON_FAILED_LENGTH_CHECK.key );

            if ( rm.getBoolean( PipelineConfigurator.USE_STANFORD_DEP ) )
            {
                StanfordDepHandler depParser = new StanfordDepHandler( posAnnotator, parseAnnotator, maxLength,
                        throwExceptionOnSentenceLengthCheck );
                viewGenerators.put(depParser.getViewName(), depParser );
            }
            if ( rm.getBoolean( PipelineConfigurator.USE_STANFORD_PARSE ) )
            {
                StanfordParseHandler parser = new StanfordParseHandler( posAnnotator, parseAnnotator, maxLength,
                        throwExceptionOnSentenceLengthCheck);
                viewGenerators.put(parser.getViewName(), parser);
            }
        }

        if (rm.getBoolean( PipelineConfigurator.USE_SRL_VERB))
        {
            Properties verbProps = new Properties();
            String verbType = SRLType.Verb.name();
            verbProps.setProperty( SrlConfigurator.SRL_TYPE.key, verbType );
            ResourceManager verbRm = new ResourceManager( verbProps );
            rm = Configurator.mergeProperties( rm, verbRm );
            try{
                SemanticRoleLabeler verbSrl = new SemanticRoleLabeler(rm, useLazyInitialization );
                viewGenerators.put( ViewNames.SRL_VERB, verbSrl );
//                viewGenerators.put(ViewNames.SRL_VERB, new SrlHandler("VerbSRL", "5.1.9", verbType, ViewNames.SRL_VERB,
//                        useLazyInitialization, rm) );


            }
            catch (Exception e)
            {
                throw new IOException("SRL verb cannot init: "+e.getMessage());
            }
        }
        if(rm.getBoolean( PipelineConfigurator.USE_SRL_NOM))
        {
            Properties nomProps = new Properties();
            String nomType = SRLType.Nom.name();
            nomProps.setProperty(SrlConfigurator.SRL_TYPE.key, nomType );
            ResourceManager nomRm = new ResourceManager( nomProps );
            rm = Configurator.mergeProperties( rm, nomRm );

            try {
                SemanticRoleLabeler nomSrl = new SemanticRoleLabeler(rm, useLazyInitialization );
                // note that you can't call nomSrl (or verbSrl).getViewName() as it may not be initialized yet
                viewGenerators.put( ViewNames.SRL_NOM, nomSrl );
//                viewGenerators.put(ViewNames.SRL_NOM,new SrlHandler("NomSRL", "5.1.9", nomType, ViewNames.SRL_NOM,
//                        useLazyInitialization, rm ));
            } catch (Exception e) {
                throw new IOException("SRL norm cannot init .."+e.getMessage());
            }
        }

        return viewGenerators;
    }
}
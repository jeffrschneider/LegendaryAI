package edu.illinois.cs.cogcomp.nlp.util;

import edu.illinois.cs.cogcomp.annotation.*;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.utilities.JsonSerializer;
import edu.illinois.cs.cogcomp.core.utilities.StringUtils;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.tokenizer.Tokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory.buildAnnotators;

/**
 * Caching Pipeline
 * This is a substitute for ehcache, which has... unhelpful... behavior when
 * the cache reaches a moderate size (say, if you want to process a thousand documents
 * with multiple annotators).  This is extremely simple local-disk-based.
 *
 * @author upadhya3
 * @author mssammon
 */
public class SimpleCachingPipeline implements AnnotatorService {

    private final Map<String, Annotator> viewProviders;
    private final boolean throwExceptionIfNotCached;
    private final boolean cacheDisabled;
    public String pathToSaveCachedFiles = null;
    private TextAnnotationBuilder textAnnotationBuilder;
    private boolean forceUpdate;
    private boolean useJson;
    private JsonSerializer jsonSerializer;

    public SimpleCachingPipeline(ResourceManager rm) throws IOException, AnnotatorException {
        this( new TokenizerTextAnnotationBuilder( new StatefulTokenizer() ),
                buildAnnotators(rm),
                rm.getString(AnnotatorServiceConfigurator.CACHE_DIR),
                rm.getBoolean(AnnotatorServiceConfigurator.THROW_EXCEPTION_IF_NOT_CACHED),
                rm.getBoolean(AnnotatorServiceConfigurator.FORCE_CACHE_UPDATE),
                rm.getBoolean(AnnotatorServiceConfigurator.DISABLE_CACHE),
                rm.getBoolean(PipelineConfigurator.USE_JSON.key)
        );
    }

    public SimpleCachingPipeline(TextAnnotationBuilder taBuilder, Map<String, Annotator> annotators,
                                 ResourceManager rm)
            throws IOException, AnnotatorException {
        this(taBuilder, annotators, rm.getString(AnnotatorServiceConfigurator.CACHE_DIR),
                rm.getBoolean(AnnotatorServiceConfigurator.THROW_EXCEPTION_IF_NOT_CACHED),
                rm.getBoolean(AnnotatorServiceConfigurator.FORCE_CACHE_UPDATE),
                rm.getBoolean(AnnotatorServiceConfigurator.DISABLE_CACHE ),
                rm.getBoolean(PipelineConfigurator.USE_JSON.key)
        );
    }



    public SimpleCachingPipeline(TextAnnotationBuilder taBuilder,
                                 Map<String, Annotator> annotators,
                                 String cacheDir,
                                 boolean throwExceptionIfNotCached,
                                 boolean forceCacheUpdate,
                                 boolean cacheDisabled,
                                 boolean useJson)
            throws IOException, AnnotatorException {
        this.viewProviders = annotators;
        this.textAnnotationBuilder = taBuilder;
        this.pathToSaveCachedFiles = cacheDir;
        this.throwExceptionIfNotCached = throwExceptionIfNotCached;
        this.forceUpdate = forceCacheUpdate;
        this.cacheDisabled = cacheDisabled;
        this.useJson = useJson;
    }

    public SimpleCachingPipeline(Map<String, Annotator> annotators,
                                 String cacheDir,
                                 boolean throwExceptionIfNotCached,
                                 boolean forceCacheUpdate,
                                 boolean cacheDisabled,
                                 boolean useJson)
            throws IOException, AnnotatorException {
        this.viewProviders = annotators;
        this.textAnnotationBuilder = new TokenizerTextAnnotationBuilder(new IllinoisTokenizer());
        this.pathToSaveCachedFiles = cacheDir;
        this.throwExceptionIfNotCached = throwExceptionIfNotCached;
        this.forceUpdate = forceCacheUpdate;
        this.cacheDisabled = cacheDisabled;
        this.useJson = useJson;
    }


    /**
     * gets a textAnnotation with the set of views specified at construction.
     * First checks for a local copy in a specific directory and if present,
     * loads it. Otherwise, creates a basic TextAnnotation from the text.
     * Checks whether specified views are present, and if not, adds them.
     * If TextAnnotation is updated, writes it to the cache in place of any original.
     * If forceUpdate is 'true', ignores cached copy and always writes the new one into the cache.
     *
     * @param text the text to annotate.
     * @return TextAnnotation populated with views specified at construction.
     */
    @Override
    public TextAnnotation createBasicTextAnnotation(String corpusId, String docId, String text) throws AnnotatorException {
        TextAnnotation ta = getCachedTextAnnotation(corpusId, docId, text);
        if (ta != null) return ta;

        return textAnnotationBuilder.createTextAnnotation(corpusId, docId, text);
    }

    @Override
    public TextAnnotation createBasicTextAnnotation(String corpusId, String docId, String text, Tokenizer.Tokenization tokenization) throws AnnotatorException {
        return null;
    }


    public TextAnnotation createBasicTextAnnotation( String corpusId, String docId,
                                                     List<String[]> tokenizedSentences) throws AnnotatorException {
        String text = joinTokenizedSentences( tokenizedSentences );

        TextAnnotation ta = getCachedTextAnnotation(corpusId, docId, text);
        if (ta != null) return ta;

        return BasicTextAnnotationBuilder.createTextAnnotationFromTokens(corpusId, docId, tokenizedSentences);
    }

    private String joinTokenizedSentences(List<String[]> tokenizedSentences) {
        StringBuilder bldr = new StringBuilder();

        for ( String[] toks : tokenizedSentences )
        {
            bldr.append( StringUtils.join( " ", toks ) );
        }

        return bldr.toString();
    }


    /**
     * Retrieves a {@link TextAnnotation} from the cache. If the cache is disabled this will return {@code null}.
     * If the {@link TextAnnotation} is not cached and {@link #throwExceptionIfNotCached} is {@code true}
     * an {@link AnnotatorException} will be thrown.
     *
     * @throws AnnotatorException
     */
    private TextAnnotation getCachedTextAnnotation(String corpusId, String docId, String text) throws AnnotatorException {
        if (cacheDisabled) return null;
        String savePath;
        try {
            savePath = getSavePath(this.pathToSaveCachedFiles, text);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AnnotatorException(e.getMessage());
        }

        TextAnnotation ta;

        if (new File(savePath).exists() && !forceUpdate) {
            try {
                ta = PipelineSerializationUtils.deserialize(Paths.get( savePath ), useJson);
                return ta;
            } catch (Exception e) {
                e.printStackTrace();
                throw new AnnotatorException(e.getMessage());
            }
        } else if (throwExceptionIfNotCached)
            throwNotCachedException(corpusId, docId, text);
        return null;
    }

    private void throwNotCachedException(String corpusId, String docId, String text) throws AnnotatorException {
        throw new AnnotatorException("text with corpusid '" + corpusId + "', docId '" + docId +
                "', value '" + text + "' was not in cache, and the field 'throwExceptionIfNotCached' is 'true'.");
    }


    /**
     * get the location of the file that corresponds to the given text and directory
     *
     * @param dir  cache root
     * @param text text used as basis of key
     * @return path to cached file location, whether it exists or not
     * @throws Exception
     */
    public static String getSavePath(String dir, String text) throws Exception {
        String md5sum = getMD5Checksum(text);
        return dir + "/" + md5sum + ".cached";
    }


    private static String getMD5Checksum(String text) throws Exception {
        MessageDigest complete = MessageDigest.getInstance("MD5");
        complete.update(text.getBytes(), 0, text.getBytes().length);
        byte[] b = complete.digest();
        String result = "";
        for (byte aB : b) result += Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
        return result;
    }


    /**
     * @param corpusId a user-specified identifier for the collection the text comes from
     * @param textId   a user-specified identifier for the text
     * @param text     the text to annotate
     * @return a TextAnnotation with views populated using the Annotators held by this object
     */
    @Override
    public TextAnnotation createAnnotatedTextAnnotation(String corpusId, String textId, String text) throws AnnotatorException {


        Set<String> viewsToAnnotate = new HashSet<>();
        viewsToAnnotate.addAll(viewProviders.keySet());
        return createAnnotatedTextAnnotation(corpusId, textId, text, viewsToAnnotate);
    }

    @Override
    public TextAnnotation createAnnotatedTextAnnotation(String corpusId, String textId, String text, Tokenizer.Tokenization tokenization) throws AnnotatorException {
        throw new IllegalStateException( "NOT YET IMPLEMENTED.");
    }

    @Override
    public TextAnnotation createAnnotatedTextAnnotation(String corpusId, String textId, String text, Set<String> viewsToAnnotate) throws AnnotatorException {

        TextAnnotation ta = createBasicTextAnnotation(corpusId, textId, text);

        ta = addViewsAndCache(ta, viewsToAnnotate, false);

        return ta;
    }

    @Override
    public TextAnnotation createAnnotatedTextAnnotation(String corpusId, String textId, String text, Tokenizer.Tokenization tokenization, Set<String> viewNames) throws AnnotatorException {
        return null;
    }

    @Override
    public void addAnnotator(Annotator annotator)
    {
        boolean isAdded = false;

        if ( !this.viewProviders.containsKey( annotator.getViewName()) )
        {
            viewProviders.put( annotator.getViewName(), annotator );
            isAdded = true;
        }

        return;
    }

    @Override
    public Set<String> getAvailableViews() {
        return viewProviders.keySet();
    }

    @Override
    public TextAnnotation annotateTextAnnotation(TextAnnotation textAnnotation, boolean clientForceUpdate) throws AnnotatorException {

        TextAnnotation ta = addViewsAndCache( textAnnotation, viewProviders.keySet(), clientForceUpdate );
        return ta;
    }

    /**
     * DOES NOT CACHE THE ADDED VIEW!!!
     *
     * @param textAnnotation textAnnotation to be modified
     * @param viewName       name of view to be added
     * @return 'true' if textAnnotation was modified
     * @throws AnnotatorException
     */
    @Override
    public boolean addView(TextAnnotation textAnnotation, String viewName) throws AnnotatorException {
        boolean isUpdated = false;

        if (ViewNames.SENTENCE.equals(viewName) || ViewNames.TOKENS.equals(viewName))
            return false;

        if ( !textAnnotation.hasView( viewName )  || forceUpdate )
        {
            isUpdated = true;

            if ( !viewProviders.containsKey( viewName ) )
                throw new AnnotatorException( "View '" + viewName + "' cannot be provided by this AnnotatorService." );

            Annotator annotator = viewProviders.get( viewName );

            for ( String prereqView : annotator.getRequiredViews() )
            {
                addView( textAnnotation, prereqView );
            }

            View v = annotator.getView(textAnnotation);

            textAnnotation.addView( annotator.getViewName(), v );
        }

        if (isUpdated && throwExceptionIfNotCached)
            throwNotCachedException(textAnnotation.getCorpusId(), textAnnotation.getId(), textAnnotation.getText());
        return isUpdated;
    }

    /**
     * add all views provided by this AnnotatorService to the specified TextAnnotation. Will overwrite if so
     *    configured.
     * @param ta
     * @return
     * @throws AnnotatorException
     */
    public TextAnnotation addAllViewsAndCache( TextAnnotation ta ) throws AnnotatorException {
        return addViewsAndCache( ta, this.viewProviders.keySet(), false );
    }

    /**
     * add all the specified views to the specified {@link TextAnnotation} and cache it. Will overwrite if so configured.
     * IMPORTANT: if the corresponding TextAnnotation has already been serialized, the argument
     *     is ignored. The client should ALWAYS use the returned TextAnnotation.
     *
     * @param ta
     * @param viewsToAnnotate
     * @return
     * @throws AnnotatorException
     */
    public TextAnnotation addViewsAndCache(TextAnnotation ta, Set<String> viewsToAnnotate, boolean clientForceUpdate) throws AnnotatorException {
        boolean isUpdated = false;


        String cacheFile;
        if (!(forceUpdate || clientForceUpdate) && !cacheDisabled)
            try {
                cacheFile = getSavePath(this.pathToSaveCachedFiles, ta.getText());
                if (IOUtils.exists(cacheFile) && !forceUpdate)
                    ta = PipelineSerializationUtils.deserialize(Paths.get( cacheFile), useJson );
            } catch (Exception e) {
                e.printStackTrace();
                throw new AnnotatorException(e.getMessage());
            }

        for (String viewName : viewsToAnnotate) {
            isUpdated = addView(ta, viewName) || isUpdated;
        }

        if (!cacheDisabled && (isUpdated || forceUpdate) || clientForceUpdate) {
            String outFile;
            try {
                outFile = getSavePath(pathToSaveCachedFiles, ta.getText());
                // must update file, so force overwrite
                PipelineSerializationUtils.serializeToFile( ta, Paths.get( outFile ), useJson, true );
            } catch (Exception e) {
                e.printStackTrace();
                throw new AnnotatorException(e.getMessage());
            }
        }
        return ta;
    }

    /**
     * change caching behavior. useful for e.g. testing
     */
    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isCacheEnabled() {
        return !cacheDisabled;
    }
}
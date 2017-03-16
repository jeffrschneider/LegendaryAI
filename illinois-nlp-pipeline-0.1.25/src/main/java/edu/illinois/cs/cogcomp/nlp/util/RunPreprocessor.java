package edu.illinois.cs.cogcomp.nlp.util;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.TextAnnotationReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * not fully implemented
 * presently just enough to help debug the system with arbitrary file, without blowing up unit tests
 */
public class RunPreprocessor
{
    private static final String NAME = RunPreprocessor.class.getCanonicalName();
    private Logger logger = LoggerFactory.getLogger( RunPreprocessor.class );

    private SimpleCachingPipeline pipeline;

    /**
     * config may contain values that override defaults.
     *
     * @param config    config file specifying pipeline parameters. May contain values that override defaults.
     * @throws Exception
     */
    public RunPreprocessor( String config ) throws Exception {
        ResourceManager nonDefaultRm = new ResourceManager( config );

        ResourceManager fullRm = ( new PipelineConfigurator() ).getConfig( nonDefaultRm );

        pipeline = new SimpleCachingPipeline( fullRm ) ;//IllinoisPipelineFactory.buildPipeline(fullRm);
    }

	public TextAnnotation runPreprocessorOnFile( String fileName ) throws FileNotFoundException, AnnotatorException {
        String text = LineIO.slurp( fileName );
        boolean forceUpdate = true; // in actual use, this will usually be 'false'
        return pipeline.createAnnotatedTextAnnotation("", "", text);
	}


    public void runPreprocessorWithCorpusReader(TextAnnotationReader reader )
    {
        Iterator<TextAnnotation> iter = reader.iterator();

        while( iter.hasNext() )
        {
            TextAnnotation ta = iter.next();
            try {
                ta = pipeline.addAllViewsAndCache( ta );
            } catch (AnnotatorException e) {
                e.printStackTrace();
                logger.error( "failed to preprocess ta with id '" + ta.getId() + "'." );
            }

            logger.debug( "processed file (assumed this is TextAnnotation docid): '" + ta.getId() + "'");
        }
    }

    /**
     * Runs a caching pipeline on an entire data set. Expects one document per file.
     * Reports files for which TextAnnotation objects could not be created.
     * Will process every non-directory file in the specified data directory, and write a
     *    file with the same name to the specified output directory in json format. It will
     *    overwrite a file with the same name in the output location.
     * In addition, TextAnnotations are written to the cache as directed by the configuration (default
     *    PipelineConfigurator/AnnotatorServiceConfigurator or config file).
     *
     *
     * @param dataDirectory directory containing source documents; may have subdirectories
     * @throws IOException
     * @throws AnnotatorException
     */
    public void runPreprocessorOnDataset( Path dataDirectory, Path outDirectory ) throws IOException, AnnotatorException {
        if ( !( dataDirectory.toFile().exists() ) )
            throw new IOException( "Directory '" + dataDirectory + "' does not exist." );
        else if ( !( dataDirectory.toFile().isDirectory() ) )
            throw new IOException( "Directory '" + dataDirectory + "' exists but is not a directory." );

//        if ( !this.pipeline.isCacheEnabled() )
//            throw new IllegalStateException( "Pipeline cache is disabled. Change the settings in the config file " +
//            "or the properties passed to the pipeline constructor." );

        FilenameFilter filter  = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File f = new File( dir.getAbsolutePath() + "/" + name );
                return f.exists() && !f.isDirectory();
            }
        };
        String[] files = IOUtils.lsFilesRecursive( dataDirectory.toString(), filter );

        for ( String f : files )
        {
            Path inPath = Paths.get( f );
            String inFileName = inPath.getName( inPath.getNameCount() - 1 ).toString();
            String outFileName = outDirectory.toString() + "/" + inFileName;
            if ( outFileName.equals( inFileName ) )
                throw new IllegalArgumentException( "Output file '" + outFileName + "' ended up same as input file '" +
                    inFileName + ". Aborting. Please check the dataDirectory and outDirectory arguments." );

            TextAnnotation ta = runPreprocessorOnFile( f );
            if ( null == ta )
                logger.warn( "Could not generate textAnnotation for file '" + f + "'." );

            else {
                logger.debug("processed file '{}', TextAnnotation id is '{}'.", f, ta.getId());
                PipelineSerializationUtils.serializeToFile( ta, Paths.get( outFileName ), true, true );
            }
        }
    }


    public static void main( String[] args )
    {
        if ( args.length != 3 )
        {
            System.err.println( "Usage: "  + NAME + " config inputFile/inputDirectory outFile/outputDirectory" );
            System.exit( -1 );
        }
        String config = args[ 0 ];
        String inFileName = args[ 1 ];
        String outFileName = args[ 2 ];

        File inFile = new File( inFileName );
        File outFile = new File( outFileName );

        if ( !inFile.exists() )
        {
            System.err.println( "input source '"+ inFileName + "' does not exist." );
            System.exit( -1 );
        }
        if ( !outFile.exists() )
        {
            System.err.println( "output file/directory '"+ outFileName + "' does not exist." );
            System.exit( -1 );
        }

        RunPreprocessor rp = null;
        try {
            rp = new RunPreprocessor( config );
        } catch (Exception e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        try {
            if ( inFile.isDirectory() )
            {
                if ( !outFile.isDirectory() )
                {
                    System.err.println( "output '" + outFileName + "' is not a directory, but input '" +
                            inFileName + "' is a directory. Input and Output must both be either files or directories." );
                }
                else
                    rp.runPreprocessorOnDataset( Paths.get( inFileName ), Paths.get( outFileName ) );
            }
            else {
                TextAnnotation ta = rp.runPreprocessorOnFile(inFileName);
                PipelineSerializationUtils.serializeToFile( ta, Paths.get( outFileName ), true, true );
                System.out.println( "Processed file.  TextAnnotation.toString(): " + ta.toString());
            }
        } catch (AnnotatorException | IOException e) {
            e.printStackTrace();
        }


    }


//	public void runPreprocessorOnDirectory( String dirName_, String outDir_ )
//	{
//
//	}
}
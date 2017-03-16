package edu.illinois.cs.cogcomp.nlp.util;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Test file processing with small set of annotators, including Stanford parser.
 * Sanity check that the documents are processed correctly.
 */
public class RunPreprocessorTest {

    private static final String CONFIG = "src/test/resources/testConfig.properties";
    private static final String TEST_FILE = "src/test/resources/tinydfcorpus/6154640fdb94510274583591cad7b379.cmp.txt";
    private static final String CORPUS_DIR = "src/test/resources/tinydfcorpus";
    private static final String TEST_OUT_DIR = "testCorpusOut";

    private static RunPreprocessor rp;
    private FilenameFilter fileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            File f = new File( dir.getAbsolutePath() + "/" + name );
            return f.exists() && !f.isDirectory();
        }
    };

    @BeforeClass
    public static void setUpBeforeClass()
    {
        try {
            rp = new RunPreprocessor(CONFIG);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit( -1 );
        }
    } 

    @AfterClass
    public static void cleanUp()
    {
        rp = null;
    }
    
    @Test
    public void testRunPreprocessorOnFile() throws Exception {
        TextAnnotation ta  = rp.runPreprocessorOnFile( TEST_FILE );

        assertNotNull( ta );
        assertTrue( ta.hasView(ViewNames.LEMMA) );
        assertTrue( ta.hasView( ViewNames.PARSE_STANFORD ));
    }

    @Test
    public void testRunPreprocessorOnDataset() {
        File outFile = new File( TEST_OUT_DIR );
        if ( !outFile.exists() )
            IOUtils.mkdir( TEST_OUT_DIR );
        else if ( !outFile.isDirectory() )
            fail( "output destination '" + TEST_OUT_DIR + "' exists, and is not a directory." );
        else
            try {
                IOUtils.cleanDir( TEST_OUT_DIR );
            } catch (IOException e) {
                e.printStackTrace();
                fail( e.getMessage() );
            }


        try {
            rp.runPreprocessorOnDataset( Paths.get(CORPUS_DIR), Paths.get( TEST_OUT_DIR) );
        } catch (IOException | AnnotatorException e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        String[] inFiles = null;
        try {
            inFiles = IOUtils.lsFilesRecursive( CORPUS_DIR, fileFilter );
        } catch (IOException e) {
            e.printStackTrace();

        }
        String[] outFiles = null;
        try {
            outFiles = IOUtils.lsFiles( TEST_OUT_DIR, fileFilter );
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertFalse( inFiles.length == 0 );

        assertEquals( inFiles.length, outFiles.length );
    }
}
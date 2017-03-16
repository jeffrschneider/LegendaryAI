package edu.illinois.cs.cogcomp.nlp.main;

import edu.illinois.cs.cogcomp.nlp.corpusreaders.XmlFragmentWhitespacingDocumentReader;
import edu.illinois.cs.cogcomp.nlp.util.RunPreprocessor;

/**
 * Created by mssammon on 8/8/16.
 */
public class PreprocessXmlFragmentCorpus {
    private static final String NAME = PreprocessXmlFragmentCorpus.class.getCanonicalName();

    public static void main(String[] args )
    {
        if ( !(args.length == 3 ) )
        {
            System.err.println( "Usage: " + NAME + " config corpusName tacCorpusDataDir" );
            System.exit( -1 );
        }
        String config = args[0];
        String corpusName = args[1];
        String corpusDir = args[2];

        XmlFragmentWhitespacingDocumentReader reader = null;
        try {
            reader = new XmlFragmentWhitespacingDocumentReader( corpusName, corpusDir );
        } catch (Exception e) {
            e.printStackTrace();
            System.exit( -1 );
        }

        RunPreprocessor rp = null;
        try {
            rp = new RunPreprocessor(config);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit( -1 );
        }

        rp.runPreprocessorWithCorpusReader( reader );

    }
}

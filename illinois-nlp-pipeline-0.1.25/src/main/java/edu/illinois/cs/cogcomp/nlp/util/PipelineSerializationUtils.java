package edu.illinois.cs.cogcomp.nlp.util;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

/**
 * transition class holding functionality that may be migrated to illinois-cogcomp-nlp/core-utilities
 *
 * @author mssammon
 */
public class PipelineSerializationUtils {

    /**
     * TODO: NOT YET IMPLEMENTED.
     * Read a cache in binary format and write it in json format.
     * @param sourceCache
     * @param targetCache
     */
    public void reformatBinaryCache( Path sourceCache, Path targetCache )
    {
        throw new IllegalStateException( "Not yet implemented." );
    }


    /**
     * TODO: NOT YET IMPLEMENTED.
     * Read a cache in json format and write it in binary format.
     * @param sourceCache
     * @param targetCache
     */
    public void reformatJsonCache( Path sourceCache, Path targetCache )
    {
        throw new IllegalStateException( "Not yet implemented." );
    }

    public static void serializeToFile(TextAnnotation ta, Path filePath, boolean useJson, boolean forceOverwrite ) throws IOException {

        if ( useJson )
        {
            String jsonTaStr =  SerializationHelper.serializeToJson(ta, true);
            if ( !forceOverwrite )
            {
                 if ( filePath.toFile().exists() )
                     throw new IOException( "ERROR: file '" + filePath.toString() + "' already exists, and " +
                             "forceOverwrite flag is set to 'false'." );
            }
            LineIO.write( filePath.toString(), Collections.singletonList(jsonTaStr));
        }
        else
            SerializationHelper.serializeTextAnnotationToFile( ta, filePath.toString(), forceOverwrite );

    }

    public static TextAnnotation deserialize(Path path, boolean useJson) throws Exception {

        TextAnnotation ta = null;
        if ( useJson ) {
            String jsonString = readRawTextFromFile( path );
            ta = SerializationHelper.deserializeFromJson(jsonString);
        }
        else
            ta = SerializationHelper.deserializeTextAnnotationFromFile( path.toString() );
        return ta;
    }

    private static String readRawTextFromFile(Path path) throws IOException {
        if ( !path.toFile().exists() )
            throw new IOException( "File '" + path.toString() + "' not found." );

        String text = LineIO.slurp( path.toString() );

        return text;
    }
}

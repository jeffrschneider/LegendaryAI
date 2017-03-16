package ai.legendary.apache;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class ApachePOS {
	private POSTaggerME tagger = null;
	public ApachePOS(final String input) throws IOException {
		final InputStream modelIn = new FileInputStream("en-pos-maxent.bin");
		final POSModel model = new POSModel(modelIn);
		tagger = new POSTaggerME(model);
	}
	
}

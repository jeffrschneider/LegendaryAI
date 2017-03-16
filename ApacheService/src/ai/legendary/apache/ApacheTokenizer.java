package ai.legendary.apache;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Pattern;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class ApacheTokenizer {
	public static final String correctionBuilder(final String input, final int[] pointers) {
		Arrays.sort(pointers);
		String result = input;
		for (int index = pointers.length-1; index >=0 ; index--) {
			final String firstpart = result.substring(0, pointers[index]);
			final String secondpart = result.substring(pointers[index]);
			result = firstpart + "<SPLIT>" + secondpart;
		}
		return result;
	}
	private String path = null;
	public ApacheTokenizer(final String source) throws Exception {
		if (source==null) {
			throw new Exception();
		}
		path = source;
		setup();
	}
	public final String[] tokenize (final String input) {
		return core.tokenize(input);
	}
	TokenizerME core = null;
	private static final Charset charset = Charset.forName("UTF-8");
	public final ApacheTokenizer setup() throws Exception{
		final ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(new File(path)), charset);
		final ObjectStream<TokenSample> sampleStream = new TokenSampleStream(lineStream);
		final TokenizerModel model = TokenizerME.train(sampleStream, new TokenizerFactory("en", new Dictionary(), true, Pattern.compile("[a-zA-Z0-9]")), TrainingParameters.defaultParams());
		core = (new TokenizerME(model));
		lineStream.close();
		sampleStream.close();
		return this;
	}
	public final ApacheTokenizer submitCorrection(final String correction) throws Exception{
		LineAdd.add(correction, path);
		setup();
		return this;
	}
}

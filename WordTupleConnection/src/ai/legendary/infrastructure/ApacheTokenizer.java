package ai.legendary.infrastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
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
	private String training = null;
	public ApacheTokenizer(final String src) throws FileNotFoundException, IOException {
		setup(src);
	}
	public ApacheTokenizer() throws FileNotFoundException, IOException {
		setup(HardCodedDirectory.result() + "tokenizer.txt");
	}
	public final String[] tokenize(final String input) {
		return (new TokenizerME(model)).tokenize(input);
	}
	private TokenizerModel model = null;
	private final void setup(final String src) throws FileNotFoundException, IOException {
		training = src;
		final Charset charset = Charset.forName("UTF-8");
		final ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(new File(src)), charset);
		final ObjectStream<TokenSample> sampleStream = new TokenSampleStream(lineStream);
		model = TokenizerME.train(sampleStream, new TokenizerFactory("en", new Dictionary(), true, Pattern.compile("[a-zA-Z0-9]")), TrainingParameters.defaultParams());
		lineStream.close();
		sampleStream.close();
		return;
	}
	public final ApacheTokenizer submitCorrection(final String inputA, final String inputB) throws IOException{
		final File f = new File(training);
		final FileWriter writer = new FileWriter(f.getAbsoluteFile(), true);
		final BufferedWriter buffer = new BufferedWriter(writer);
		buffer.write(inputA + "<SPLIT>" + inputB);
		buffer.close();
		writer.close();
		setup(training);
		return this;
	}
}

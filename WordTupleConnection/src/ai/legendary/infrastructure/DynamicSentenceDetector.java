package ai.legendary.infrastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class DynamicSentenceDetector {
	private String trainingFile = null;
	private SentenceDetectorME sentenceDetector = null;
	private static final Charset charset = Charset.forName("UTF-8");
	@SuppressWarnings("deprecation")
	public void reload() throws FileNotFoundException, IOException {
		final ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(new File(trainingFile)), charset);
		final ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);
		final SentenceModel loader = SentenceDetectorME.train("en", sampleStream, true, null, TrainingParameters.defaultParams());
		lineStream.close();
		sampleStream.close();
		sentenceDetector = new SentenceDetectorME(loader);
		return;
	}
	public void AddString(final String input) throws IOException {
		final File f = new File(trainingFile);
		final FileWriter writer = new FileWriter(f.getAbsoluteFile(), true);
		final BufferedWriter buffer = new BufferedWriter(writer);
		buffer.write(input);
		buffer.write("\r\n");
		buffer.close();
		writer.close();
		return;
	}
	public String[] sentDetect (final String input) {
		return sentenceDetector.sentDetect(input);
	}
	public Span[] sentPosDetect(final String input) {
		return sentenceDetector.sentPosDetect(input);
	}
	public DynamicSentenceDetector (final String location) throws FileNotFoundException, IOException {
		trainingFile = location;
		reload();
	}
}

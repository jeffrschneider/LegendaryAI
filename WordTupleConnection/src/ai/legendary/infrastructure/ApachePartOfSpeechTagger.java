package ai.legendary.infrastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class ApachePartOfSpeechTagger {
	POSModel model = null;
	String dataFile = null;
	public ApachePartOfSpeechTagger(final String src) throws Exception {
		reset(src);
	}
	public ApachePartOfSpeechTagger() throws Exception {
		reset(HardCodedDirectory.result() + "PartOfSpeech.txt");
	}
	private final void reset(final String src) throws Exception {
		dataFile = src;
		final ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(new File(src)), "UTF-8");
		final ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
		model = POSTaggerME.train("en", sampleStream, TrainingParameters.defaultParams(), new POSTaggerFactory());
	}
	public final String analyze(final String input) throws FileNotFoundException, IOException {
		final PartOfSpeechObject result = new PartOfSpeechObject();
		return result.determine(new POSTaggerME(model), (new ApacheTokenizer()).tokenize(input)).JSONexport();
	}
	public final String analyze(final String[] input) throws FileNotFoundException, IOException {
		final PartOfSpeechObject result = new PartOfSpeechObject();
		return result.determine(new POSTaggerME(model), input).JSONexport();
	}
	public final ApachePartOfSpeechTagger submitCorrections(final String[] words, final String[] parts) throws Exception {
		final SpeedConcat sc = new SpeedConcat();
		for (int index = 0; index < words.length; index++) {
			sc.append(words[index]).append("_").append(parts[index]);
		}
		final String addition = sc.concat();

		final File f = new File(dataFile);
		final FileWriter writer = new FileWriter(f.getAbsoluteFile(), true);
		final BufferedWriter buffer = new BufferedWriter(writer);
		buffer.write(addition);
		buffer.close();
		writer.close();
		reset(dataFile);
		return this;
	}
	
}

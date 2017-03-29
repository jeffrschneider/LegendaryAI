package ai.legendary.infrastructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;

import ai.legendary.core.SpeedConcat;
import ai.legendary.core.stringUtils;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class ApacheNameIdentifier {
	private String path = null;
	private NameFinderME nameFinder = null;
	private boolean training = false;
	private final static Charset charset = Charset.forName("UTF-8");
	public ApacheNameIdentifier(final String input) throws Exception {
		path = input;
		training = true;
		reload();
	}
	public ApacheNameIdentifier(final String input, final boolean train) throws Exception {
		path = input;
		training = train;
		if (training==true) {
			reload();
			return;
		}
		final InputStream modelIn = new FileInputStream(input);
		final TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
		modelIn.close();
		nameFinder = new NameFinderME(model);
	}
	private void reload() throws Exception{
		if (training==false) {
			throw new Exception();
		}
		final ObjectStream<String> lineStream = new PlainTextByLineStream( new MarkableFileInputStreamFactory(new File(path)), charset);
		final ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
		final TokenNameFinderModel model = NameFinderME.train("en", "person", sampleStream, TrainingParameters.defaultParams(), new TokenNameFinderFactory());
		sampleStream.close();
		lineStream.close();
		nameFinder = new NameFinderME(model);
	}
	public final Identifier[] getNames(final String[] input) throws Exception {
		final Span[] spans = nameFinder.find(input);
		final LinkedList<Identifier> result = new LinkedList<Identifier>();
		for (int index = 0; index < spans.length; index++) {
			result.add(Identifier.MakeIdentifier(spans[index], input));
		}
		return (result.toArray(new Identifier[result.size()]));
	}
	public final ApacheNameIdentifier addData(final String sentence) throws Exception {
		if (training==false) {
			throw new Exception();
		}
		if (sentence==null) {
			throw new Exception();
		}
		if (sentence.equals("")) {
			throw new Exception();
		}
		LineAdd.add(sentence, path);
		return this;
	}
	public static final String yieldIdentifyingSentence(final String sentence, final String Name, final String type) throws Exception {
		if (sentence==null) {
			throw new Exception();
		}
		if (Name==null) {
			throw new Exception();
		}
		if (Name.equals("")) {
			throw new Exception();
		}
		if (type==null) {
			return yieldIdentifyingSentence(sentence, Name, "noun");
		}
		if (type.equals("")) {
			return yieldIdentifyingSentence(sentence, Name, "noun");
		}
		final String[] broken = sentence.split(Name);
		final String combinedIdentifier = ("<START:" + type) + (("> " + Name) + " <END>");
		return stringUtils.join(broken, combinedIdentifier);
	}
	public static final String JSONbuild(final String miscData, final Identifier[] nameData) {
		if (nameData==null) {
			return JSONbuild(miscData, new Identifier[]{});
		}
		final SpeedConcat result = new SpeedConcat().append("{");
		if (miscData != null) {
			result.append(miscData).append(",");
		}
		result.append("\"identifiers\":[");
		if (nameData.length > 0) {
			result.append(nameData[0].JSONexport());
		}
		for (int index = 1; index < nameData.length; index++) {
			result.append(", ").append(nameData[index].JSONexport());
		}
		return result.append("]}").concat();
	}
}

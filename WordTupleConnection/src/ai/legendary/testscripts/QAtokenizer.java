package ai.legendary.testscripts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;

import ai.legendary.infrastructure.ApachePartOfSpeechDetector;
import general.DataBlob;

public class QAtokenizer {
	public static final void main(final String[] args) throws IOException {
		final String dir = "C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\WordTupleConnection\\";
		final String contents = new String(Files.readAllBytes(Paths.get("C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\dev-v1.1.json")));
		final Gson gson = new Gson();
		final DataBlob anotherStr = gson.fromJson(contents, DataBlob.class);
		final ApachePartOfSpeechDetector aposd = new ApachePartOfSpeechDetector(dir + "en-pos-maxent.bin", dir + "en-token.bin");
		final String[] tokens = aposd.tag(aposd.tokenize(anotherStr.data[0].paragraphs[0].context));
		for (int index = 0; index < tokens.length; index++) {
			System.out.println(tokens[index]);
		}
	}
}

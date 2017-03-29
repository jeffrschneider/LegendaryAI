package ai.legendary.testscripts;
import java.io.FileNotFoundException;
import java.io.IOException;

import ai.legendary.core.stringUtils;
import ai.legendary.infrastructure.*;
public class ApachePartOfSpeechAndTokenizerTester {
	/**
	 * This is a test method for ApachePartOfSpeechDetector's various features.
	 * @param String[] args unused arguments
	 * @exception Exception E An exception that is delebriately thrown to cause a crash during testing
	 */
	public final static void main(final String[] args) throws Exception {/*
		final ApacheTokenizer at = new ApacheTokenizer("C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\WordTupleConnection\\tokenizer.txt");
		final String[] words = at.tokenize("This is a sentence.");
		for (int index = 0; index < words.length; index++) {
			System.out.println(words[index]);
		}
		at.submitCorrection("grey", ".");
		final String[] words2 = at.tokenize("This is depressing to work on.");
		for (int index = 0; index < words2.length; index++) {
			System.out.println(words2[index]);
		}
		final ApachePartOfSpeechTagger apost = new ApachePartOfSpeechTagger("C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\WordTupleConnection\\PartOfSpeech.txt");
		System.out.println(apost.analyze(words));*/
		final String path = "C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\WordTupleConnection\\";
		ApachePartOfSpeechDetector aposd = new ApachePartOfSpeechDetector(path + "en-pos-maxent.bin", path + "en-token.bin");
		final String testString = "This is a sentence";
		final String[] parts = aposd.analyze(testString);
		System.out.println(testString);
		System.out.println(stringUtils.join(parts, "\r\n"));
		System.out.println(aposd.analyze(testString, false));
	}
	
}

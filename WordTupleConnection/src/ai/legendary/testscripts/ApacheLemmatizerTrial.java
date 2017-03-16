package ai.legendary.testscripts;

import java.io.IOException;

import ai.legendary.infrastructure.GeneralLemmatizer;
import ai.legendary.infrastructure.LemmaResult;
import opennlp.tools.util.InvalidFormatException;

public class ApacheLemmatizerTrial {
	public static final void main(final String[] args) throws InvalidFormatException, IOException {
		final String path = "C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\WordTupleConnection\\";
		final GeneralLemmatizer gl = new GeneralLemmatizer(path + "en-lemmatizer.dict");
		final String[] tokens = new String[] { "Rockwell", "International", "Corp.", "'s",
			    "Tulsa", "unit", "said", "it", "signed", "a", "tentative", "agreement",
			    "extending", "its", "contract", "with", "Boeing", "Co.", "to",
			    "provide", "structural", "parts", "for", "Boeing", "'s", "747",
			    "jetliners", "." };

		final String[] postags = new String[] { "NNP", "NNP", "NNP", "POS", "NNP", "NN",
			    "VBD", "PRP", "VBD", "DT", "JJ", "NN", "VBG", "PRP$", "NN", "IN",
			    "NNP", "NNP", "TO", "VB", "JJ", "NNS", "IN", "NNP", "POS", "CD", "NNS",
			    "." };
		System.out.println(LemmaResult.JSONexport(gl.Lemmatize(tokens, postags), "\"StatusCode\": 1"));
		return;
	}
}

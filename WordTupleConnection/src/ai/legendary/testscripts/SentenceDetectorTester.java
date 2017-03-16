package ai.legendary.testscripts;
import java.io.FileNotFoundException;
import java.io.IOException;

import ai.legendary.infrastructure.DynamicSentenceDetector;
public class SentenceDetectorTester {

	/**
	 * This is a test method for DynamicSentenceDetector's various features.
	 * @param String[] args unused arguments
	 * @exception FileNotFoundException E An exception that is delebriately thrown to cause a crash during testing
	 * @exception IOException E An exception that is delebriately thrown to cause a crash during testing
	 */
	public static final void main(final String[] args) throws FileNotFoundException, IOException {
		//final DynamicSentenceDetector dsd = new DynamicSentenceDetector("C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\WordTupleConnection\\WebContent\\WEB-INF\\SentenceCalibrationData.txt");
		final DynamicSentenceDetector dsd = new DynamicSentenceDetector("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\WordTupleConnection\\WEB-INF\\SentenceCalibrationData.txt");
		dsd.AddString("80s clothing was a mistake.");
		dsd.reload();
		final String[] attempt = dsd.sentDetect("Is this a sentence? I need to calibrate this more. Anything is possible.");
		for (int index = 0; index < attempt.length; index++) {
			System.out.println(attempt[index]);
		}
		return;
	}
}

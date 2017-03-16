package ai.legendary.apache;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

@WebServlet(
		urlPatterns = "/1.7.2/SentenceDetect/*"
		)
public class SentenceService extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SentenceDetectorME dsd = null;
	final static String trainingFile = DataDir.result() + "SentenceCalibrationData.txt";
	private void reload() {
		try {
			final Charset charset = Charset.forName("UTF-8");
			final ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(new File(trainingFile)), charset);
			final ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);
			final SentenceModel loader = SentenceDetectorME.train("en", sampleStream, true, null, TrainingParameters.defaultParams());
			lineStream.close();
			sampleStream.close();
			dsd = new SentenceDetectorME(loader);
		} catch (final Exception e) {
			reload();
		}
		return;
	}
	public void init() throws ServletException {
		reload();
		return;
	}
	public void destory() {}
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String action = request.getParameter("action");
		if (action==null) {
			MissingParam.missingParamJob("action", response);
			return;
		}
		final String data = request.getParameter("data");
		if (data==null) {
			MissingParam.missingParamJob("data", response);
			return;
		}
		try {
			switch (action) {
				case "train":
					LineAdd.add(stringUtils.urlDecode(data), trainingFile);
					reload();
					MissingParam.succeededUpdate(response);
					return;
				case "detect":
					final String paragraph = stringUtils.urlDecode(data);
					final String[] sentences = dsd.sentDetect(paragraph);
					final PrintWriter pw = response.getWriter();
					pw.print("{\"statusCode\": 1,\"results\":[");
					if (sentences.length > 0) {
						pw.print("\"");
						pw.print(stringUtils.join(sentences[0].split("\""), "\\\""));
						pw.print("\"");
					}
					for (int index = 1; index < sentences.length; index++) {
						pw.print(",\"");
						pw.print(stringUtils.join(sentences[index].split("\""), "\\\""));
						pw.print("\"");
					}
					pw.print("]}");
					pw.close();
					return;
				default:
					MissingParam.missingParamJob("action", response);
					return;
			}
		} catch (final Exception e) {
			MissingParam.error(e.toString(), response);
			return;
		}
		
		
	}
}

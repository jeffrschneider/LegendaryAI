package ai.legendary.apache;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.stringUtils;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

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
	final static String binFile = DataDir.result() + "en-sent.bin";
	private void reload() {
		try {
			final InputStream modelIn = new FileInputStream(binFile);
			final SentenceModel model = new SentenceModel(modelIn);
			modelIn.close();
			dsd = new SentenceDetectorME(model);
		} catch (final Exception e) {
			reload();
		}
		return;
	}
	@Override
	public void init() throws ServletException {
		reload();
		return;
	}
	public void destory() {}
	@Override
	public void doGet (final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		final String data = request.getParameter("data");
		if (data==null) {
			MissingParam.missingParamJob("data", response);
			return;
		}
		try {
			final String paragraph = stringUtils.urlDecode(data);
			final String[] sentences = dsd.sentDetect(paragraph);
			MissingParam.sentenceResult(response, sentences);
			return;
		} catch (final Exception e) {
			MissingParam.error(e.toString(), response);
			return;
		}
	}
}

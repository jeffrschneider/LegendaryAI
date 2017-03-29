package ai.legendary.apache;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.stringUtils;

@WebServlet(
		urlPatterns = "/1.7.2/NounDetect/*"
		)
public class NounService extends HttpServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String configFile = DataDir.result() + "fileNames.txt";
	private ApacheNameIdentifier ani = null;
	public void init(){
		try {
			loadDetectors(configFile);
		} catch (final Exception e) {
			
		}
	}
	private void loadDetectors(final String input) throws Exception {
		final String[] fileNames = LineAdd.readFileAsLines(input);
		for (int index = 0; index < fileNames.length; index++) {
			fileNames[index] = DataDir.result() + fileNames[index];
		}
		ani = new ApacheNameIdentifier(fileNames, DataDir.result() + "tokenizerData.txt");
	}
	public void destory() {}
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String action = request.getParameter("action");
		if (action==null) {
			MissingParam.missingParamJob("action", response);
			return;
		}
		final PrintWriter pw = response.getWriter();
		try {
			switch (action) {
				case "reset":
					loadDetectors(configFile);
					MissingParam.succeededUpdate(response);
					return;
				case "locate":
					final String raw = request.getParameter("text");
					if (raw==null) {
						MissingParam.missingParamJob("text", response);
						return;
					}
					final String text = stringUtils.urlDecode(raw);
					final Noun[] results = ani.findNames(text);
					pw.print("{\"statusCode\": 1, \"results\":[");
					if (results.length > 0) {
						pw.print(results[0].JSONexport());
					}
					for (int index = 1; index < results.length; index++) {
						pw.print(", ");
						pw.print(results[index].JSONexport());
					}
					pw.print("]}");
					pw.close();
					return;
				default:
					MissingParam.missingParamJob("action", response);
					return;
			}
		} catch (final Exception e) {
			e.printStackTrace(pw);
			pw.println(ani==null);
			MissingParam.serverError(response);
			return;
		}
	}
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String action = request.getParameter("action");
		if (action==null) {
			MissingParam.missingParamJob("action", response);
			return;
		}
		final PrintWriter pw = response.getWriter();
		try {
			switch (action) {
				case "update":
					final String fileName = System.currentTimeMillis() + ".bin";
					final InputStream in = request.getInputStream();
					LineAdd.dumpDataToFile(fileName, in);
					LineAdd.add(configFile, fileName);
					loadDetectors(configFile);
					MissingParam.succeededUpdate(response);
					return;
				default:
					MissingParam.missingParamJob("action", response);
					return;
			}
		} catch (final Exception e) {
			MissingParam.serverError(response);
			return;
		}
		
	}
}

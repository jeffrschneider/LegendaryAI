package ai.legendary.infrastructure;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.stringUtils;

public abstract class InvalidVersionOrService extends GenericService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public void init() {}

	@Override
	public void destroy() {}

	@Override
	public String[] getMandatoryParams() {
		return new String[]{};
	}
	private static final String[] getServicesForVersion(final String provider, final String version) {
		ProviderSwitch:
		switch(provider) {
			case "Apache":
				ApacheSwitch:
				switch (version) {
					case "1.7.2":
						return new String[]{"SentenceIndexes", "PartOfSpeech", "LemmaJob"};
					default:
						return new String[]{};
				}
			default:
				return new String[]{};
		}
	}
	private void handleProvider(final String provider, final PrintWriter writer, final String version) {
		writer.write("{\"version\": \"");
		writer.write(version);
		writer.write("\", \"services\":[");
		final String[] services = getServicesForVersion(provider, version);
		if (services.length > 0) {
			writer.write("\"");
			writer.write(stringUtils.join(services, "\",\""));
			writer.write("\"");
		}
		writer.write("]}");
	}
	private void providerJob(final PrintWriter writer, final String provider, final String[] versions) {
		writer.write("\"" + provider + "\": [");
		if (versions.length==0) {
			writer.write("]");
			return;
		}
		handleProvider(provider, writer, versions[0]);
		for (int index = 1; index < versions.length; index++) {
			writer.write(",");
			handleProvider(provider, writer, versions[index]);
		}
		writer.write("]");
		return;
	}
	private static final String[] ApacheVersions = new String[]{"1.7.2"}; 
	@Override
	public void filteredGet(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter writer) throws Exception {
		writer.write("{\"statusCode\": -1, \"problem\": \"Invalid service version or service task. Valid versions and tasks attached.\",");
		providerJob(writer, "Apache", ApacheVersions);
		writer.write("}");
		writer.close();
	}

}

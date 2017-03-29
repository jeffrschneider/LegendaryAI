package ai.legendary.infrastructure;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;

public abstract class GenericService extends HttpServlet {
	private static long serialVersionUID;
	abstract public void init();
	abstract public void destroy();
	abstract public String[] getMandatoryParams();
	abstract public void filteredGet(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter writer) throws Exception;
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String[] params = getMandatoryParams();
		for (int index = 0; index < params.length; index++) {
			final String param = request.getParameter(params[index]);
			if (param==null) {
				MissingParam.missingParamJob(params[index], response);
				return;
			}
		}
		try {
			filteredGet(request, response, response.getWriter());
			return;
		} catch (final Exception e) {
			MissingParam.serverError(response);
			return;
		}
	}
}

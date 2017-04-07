package ai.legendary.stanford;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.legendary.core.MissingParam;
import ai.legendary.core.SROset;
import ai.legendary.core.stringUtils;
@WebServlet(
		urlPatterns = "/3.7.0/Relationship/*"
		)
public class RelationshipServer extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RelationshipService rs = new RelationshipService();
	public void init(){}
	public void destroy(){}
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String rt = request.getParameter("text");
		if (rt==null) {
			MissingParam.missingParamJob("text", response);
			return;
		}
		try {
			final String text = stringUtils.urlDecode(rt);
			final LinkedList<SROset> sets = rs.getSROsets(text);
			MissingParam.SROsetResult(response, sets);
			return;
		} catch (final Exception e) {
			e.printStackTrace(response.getWriter());
			response.getWriter().close();
			//MissingParam.serverError(response);
			return;
		}
	}

}

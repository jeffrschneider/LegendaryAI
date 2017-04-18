package ai.legendary.IES;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ai.legendary.core.InformationExtractorResult;
import de.mpii.clausie.ClausIE;
import de.mpii.clausie.Proposition;

public class clausIE {
	public clausIE(){
		
	}
	public final InformationExtractorResult[] determine (final String input) {
		final ClausIE cie = new de.mpii.clausie.ClausIE();
		cie.initParser();
		cie.parse(input);
		cie.detectClauses();
		cie.generatePropositions();
		final LinkedList<InformationExtractorResult> result = new LinkedList<InformationExtractorResult>();
		final List<Proposition> propositions = cie.getPropositions();
		final Iterator<Proposition> i = propositions.iterator();
		while (i.hasNext()) {
			final Proposition p = i.next();
			final InformationExtractorResult intermediate = new InformationExtractorResult().setSubject(p.subject()).setRelation(p.relation());
			if (p.noArguments() > 0) {
				intermediate.setObject(p.argument(0));
			}
			result.add(intermediate);
		}
		return result.toArray(new InformationExtractorResult[result.size()]);
	}
}

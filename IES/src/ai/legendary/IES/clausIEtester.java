package ai.legendary.IES;

import ai.legendary.core.InformationExtractorResult;

public class clausIEtester {
	public static final void main (final String[] args) {
		final clausIE cie = new clausIE();
		final InformationExtractorResult[] iers = cie.determine("Alice and Bob are both talking to Eve and Dereck");
		for (final InformationExtractorResult ier: iers) {
			System.out.print("context: ");
			System.out.println(ier.getContext());
			System.out.print("subject: ");
			System.out.println(ier.getSubject());
			System.out.print("relation: ");
			System.out.println(ier.getRelation());
			System.out.print("object: ");
			System.out.println(ier.getObject());
			System.out.println("");
		}
	}
}

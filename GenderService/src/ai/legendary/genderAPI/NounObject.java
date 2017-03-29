package ai.legendary.genderAPI;

import java.io.PrintWriter;

public class NounObject {
	public GenderService.genderData[] genders = null;
	public String[] nameParts = null;
	public NounObject(){
		
	}
	public NounObject JSONprint(final PrintWriter printer) throws Exception {
		if (genders==null) {
			throw new Exception();
		}
		if (nameParts==null) {
			throw new Exception();
		}
		printer.print("{\"name\":[");
		if (nameParts.length > 0) {
			printer.print("\"");
			printer.print(nameParts[0].replaceAll("\"", "\\\"").replaceAll("\'", "\\\'"));
			printer.print("\"");
		}
		for (int index = 1; index < nameParts.length; index++) {
			printer.print(", \"");
			printer.print(nameParts[index].replaceAll("\"", "\\\"").replaceAll("\'", "\\\'"));
			printer.print("\"");
		}
		printer.print("], \"gender\":[");
		if (genders.length > 0) {
			printer.print("\"");
			printer.print(GenderService.genderExport(genders[0]));
			printer.print("\"");
		}
		for (int index = 1; index < genders.length; index++) {
			printer.print(",\"");
			printer.print(GenderService.genderExport(genders[index]));
			printer.print("\"");
		}
		printer.print("]}");
		return this;
	}
}

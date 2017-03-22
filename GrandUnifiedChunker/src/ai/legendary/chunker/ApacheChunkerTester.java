package ai.legendary.chunker;

public class ApacheChunkerTester {
	public static final void main(final String[] args) throws Exception {
		final String dir = "C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\GrandUnifiedChunker\\WebContent\\WEB-INF\\";
		final ApacheChunker apache = ApacheChunker.BuildApacheChunkerFromBins(dir + "apacheTokens.bin", dir + "apachePOS.bin", dir + "apacheChunks.bin");
		final ChunkerResult cr = apache.chunk("This is a simple sentence.");
		System.out.println(ChunkerResult.jsonExport(cr));
		final StanfordChunker sc = new StanfordChunker();
		final ChunkerResult sr = sc.chunk("We are going to watch a screwed up movie about sex, drugs, violence, and puppets; and then we are going to McDonalds.");		
		System.out.println(ChunkerResult.jsonExport(sr));
		final ChunkerResult sr2 = sc.chunk("It is illegal to grow pot, and yet, manufacturing raisins is a lawful activity. Our society has bad priorities.");		
		System.out.println(ChunkerResult.jsonExport(sr2));
		final ChunkerResult cr2 = apache.chunk("It is illegal to grow pot, and yet, manufacturing raisins is a lawful activity. Our society has bad priorities.");		
		System.out.println(ChunkerResult.jsonExport(cr2));
		return;
	}
}

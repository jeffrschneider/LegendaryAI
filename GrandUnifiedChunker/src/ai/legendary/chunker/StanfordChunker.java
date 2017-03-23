package ai.legendary.chunker;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordChunker {
	public StanfordChunker() {
		final Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, parse");
		pipeline = new StanfordCoreNLP(props);
	}
	private StanfordCoreNLP pipeline = null;
	public final ChunkerResult chunk(final String input) throws Exception {
		final Annotation doc = new Annotation(input);
		pipeline.annotate(doc);
		final ChunkerResult result = new ChunkerResult();
		result.vendor = "Stanford";
		final LinkedList<Integer> indexes = new LinkedList<Integer>();
		final LinkedList<String> tokens = new LinkedList<String>();
		final LinkedList<String> partsOfSpeech = new LinkedList<String>();
		final LinkedList<String> sections = new LinkedList<String>();
		
		int tokenIndex = 0;
	    final List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for (final CoreMap sentence: sentences) {
			//indexes.add(new Integer(tokenIndex));
			for (final CoreLabel token: sentence.get(TokensAnnotation.class)) {
				tokens.add(token.get(TextAnnotation.class));
				partsOfSpeech.add(token.get(PartOfSpeechAnnotation.class));
				tokenIndex++;
			}
			final Tree tree = sentence.get(TreeAnnotation.class);
			tree.indentedXMLPrint();
			parseBranches(tokens, indexes, sections, tree, tokenIndex);
		    //System.out.println("parse tree:\n" + tree);
			//indexes.add(new Integer(tokenIndex));
		}
	    
		result.tokens = tokens.toArray(new String[tokens.size()]);
		result.parts = partsOfSpeech.toArray(new String[partsOfSpeech.size()]);
		result.sections = sections.toArray(new String[sections.size()]);
		result.indexes = ChunkerResult.IntegerListToIntArray(indexes);
		return result;
	}
	public final static void parseBranches(final List<String> tokens, final List<Integer> indexes, final List<String> sections, final Tree tree, int initialIndex) throws Exception {
		condensedVoidCheck(tokens, indexes, sections, tree);
		if (tree.isEmpty()) {
			return;
		}
		if (tree.isLeaf()) {
			indexes.add(new Integer(initialIndex));
			sections.add(tree.value());
			indexes.add(new Integer(initialIndex+1));
			return;
		}
		if (tree.numChildren()==1) {
			parseBranches(tokens, indexes, sections, tree.children()[0], initialIndex);
			return;
		}
		if (shouldBeCondensed(tree)) {
			if (indexes.size()==0) {
				condenseBranch(tokens, indexes, sections, tree, 0);
			} else {
				condenseBranch(tokens, indexes, sections, tree, indexes.get(indexes.size()-1).intValue());
			}
			return;
		}
		final Tree[] kids = tree.children();
		for (int index = 0; index < kids.length; index++) {
			if (indexes.size()==0) {
				parseBranches(tokens, indexes, sections, kids[index], 0);
			} else {
				parseBranches(tokens, indexes, sections, kids[index], indexes.get(indexes.size()-1).intValue());
			}
		}
		return;
	}
	public static final void nullKill(final Object o) throws Exception{
		if (o==null) {
			throw new Exception();
		}
		return;
	}
	public static final boolean shouldBeCondensed(final Tree tree) throws Exception {
		nullKill(tree);
		if (containsPuncBreaker(tree)) {
			return false;
		}
		if (tree.value().startsWith("N")) {
			if (!(containsBranchType(tree.children(), "V.*"))) {
				return true;
			}
		}
		if (tree.value().startsWith("V")) {
			if (!(containsBranchType(tree.children(), "N.*"))) {
				return true;
			}
		}
		return false;
	}
	public static final boolean containsBranchType(final Tree[] trees, final String type) throws Exception {
		nullKill(trees);
		nullKill(type);
		for (final Tree kid: trees) {
			if (containsBranchType(kid, type)) {
				return true;
			}
		}
		return false;
	}
	public static final boolean typeMatch(final Tree tree, final String type) throws Exception {
		nullKill(type);
		if (tree.isEmpty()) {
			return false;
		}
		if (tree.isLeaf()) {
			return false;
		}
		if (tree.value().equals(type)) {
			return true;
		}
		if (tree.value().matches(type)) {
			return true;
		}
		return false;
	}
	public static final boolean containsBranchType(final Tree tree, final String type) throws Exception {
		nullKill(type);
		nullKill(tree);
		if (typeMatch(tree, type)) {
			return true;
		}
		final Tree[] kids = tree.children();
		return containsBranchType(kids, type);
	}
	public final static void condenseBranch(final List<String> tokens, final List<Integer> indexes, final List<String> sections, final Tree tree, int initialIndex) throws Exception {
		condensedVoidCheck(tokens, indexes, sections, tree);
		if (tree.isEmpty()) {
			return;
		}
		if (tree.isLeaf()) {
			indexes.add(new Integer(initialIndex));
			sections.add(tree.value());
			initialIndex++;
			indexes.add(new Integer(initialIndex));
			return;
		}
		indexes.add(new Integer(initialIndex));
		final SpeedConcat sc = new SpeedConcat().append("");
		final Stack<Tree> stack = new Stack<Tree>();
		stack.add(tree);
		while (!(stack.isEmpty())) {
			final Tree ptr = stack.pop();
			if (ptr.isLeaf()) {
				if (ptr.value() == null) {
					throw new Exception();
				}
				sc.append(" ").append(ptr.value());
				initialIndex++;
				continue;
			}
			if (ptr.children().length > 0) {
				final Tree[] branches = ptr.children();
				for (int index = branches.length -1; index > -1; index-- ) {
					stack.push(branches[index]);
				}
			}
		}
		sections.add(sc.concat());
		indexes.add(new Integer(initialIndex));
	}
	public final static void condensedVoidCheck(final List<String> tokens, final List<Integer> indexes, final List<String> sections, final Tree tree) throws Exception {
		nullKill(tokens);
		nullKill(indexes);
		nullKill(sections);
		nullKill(tree);
		return;
	}
	public static final boolean containsPuncBreaker(final Tree[] branches) throws Exception {
		for (final Tree branch: branches) {
			if (containsPuncBreaker(branch)) {
				return true;
			}
		}
		return false;
	}
	public static final boolean containsPuncBreaker(final Tree branch) throws Exception {
		nullKill(branch);
		final String label = branch.value();
		innerSwitch:
		switch (label) {
			case ":":
			case ".":
			case ",":
				return true;
			default:
				if (!(branch.isLeaf())) {
					final boolean internal = containsPuncBreaker(branch.children());
					if (internal==true) {
						return true;
					}
				}
				break innerSwitch;
		}
		return false;
	}
}

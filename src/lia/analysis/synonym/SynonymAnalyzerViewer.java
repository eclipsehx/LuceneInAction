package lia.analysis.synonym;

import lia.analysis.AnalyzerUtils;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

// From chapter 4

public class SynonymAnalyzerViewer {

	private static final String text = "The quick brown fox jumps over the lazy dog";

	@Test
	public void testSynonymEngine() throws IOException {

		SynonymEngine engine = new TestSynonymEngine();

		AnalyzerUtils.displayTokensWithPositions(new SynonymAnalyzer(engine), text);

		AnalyzerUtils.displayTokensWithPositions(new SynonymAnalyzer(engine), "\"Oh, we get both kinds - country AND western!\" - B.B.");
	}

	@Test
	public void testWordNetSynonymEngine() throws IOException {

		String wordnetIndexPath = "./index/wordnet";

		SynonymEngine engine = new WordNetSynonymEngine(new File(wordnetIndexPath));

		AnalyzerUtils.displayTokensWithPositions(new SynonymAnalyzer(engine), text);
	}
}

package lia.analysis.stopanalyzer;

import junit.framework.TestCase;
import lia.analysis.AnalyzerUtils;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.util.Version;
import org.junit.Test;

// From chapter 4

public class StopAnalyzerTest extends TestCase {

	private StopAnalyzer stopAnalyzer = new StopAnalyzer(Version.LUCENE_30);

	@Test
	public void testHoles() throws Exception {

		String[] expected = { "one", "enough" };

		AnalyzerUtils.assertAnalyzesTo(stopAnalyzer, "one is not enough", expected);
		AnalyzerUtils.assertAnalyzesTo(stopAnalyzer, "one is enough", expected);
		AnalyzerUtils.assertAnalyzesTo(stopAnalyzer, "one enough", expected);
		AnalyzerUtils.assertAnalyzesTo(stopAnalyzer, "one but not enough", expected);
	}

	public static void main(String[] args) throws Exception {

		// 打印出 StopAnalyzer.ENGLISH_STOP_WORDS_SET
		System.out.println(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
	}
}

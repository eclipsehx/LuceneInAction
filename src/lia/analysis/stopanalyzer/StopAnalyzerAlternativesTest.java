package lia.analysis.stopanalyzer;

import org.junit.Test;

import junit.framework.TestCase;
import lia.analysis.AnalyzerUtils;

// From chapter 4

public class StopAnalyzerAlternativesTest extends TestCase {

	@Test
	public void testStopAnalyzer2() throws Exception {
		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer2(), "The quick brown...", new String[] { "quick", "brown" });
	}

	@Test
	public void testStopAnalyzerFlawed() throws Exception {
		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzerFlawed(), "The quick brown...", new String[] { "the", "quick", "brown" });
	}

	/**
	 * Illustrates that "the" is not removed, although it is lowercased
	 */
	public static void main(String[] args) throws Exception {
		AnalyzerUtils.displayTokens(new StopAnalyzerFlawed(), "The quick brown...");
	}
}

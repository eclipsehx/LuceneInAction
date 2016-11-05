package lia.analysis.queryparser;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

// From chapter 4

public class AnalysisParalysisTest extends TestCase {

	public void testAnalyzer() throws Exception {

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		String queryString = "category:/philosophy/eastern";

		Query query = new QueryParser(Version.LUCENE_30, "contents", analyzer).parse(queryString);

		assertEquals("path got split, yikes!", "category:\"philosophy eastern\"", query.toString("contents"));

		PerFieldAnalyzerWrapper perFieldAnalyzer = new PerFieldAnalyzerWrapper(analyzer);
		perFieldAnalyzer.addAnalyzer("category", new WhitespaceAnalyzer());

		query = new QueryParser(Version.LUCENE_30, "contents", perFieldAnalyzer).parse(queryString);

		assertEquals("leave category field alone", "category:/philosophy/eastern", query.toString("contents"));
	}
}

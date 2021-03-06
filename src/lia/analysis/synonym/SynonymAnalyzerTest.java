package lia.analysis.synonym;

import java.io.StringReader;

import junit.framework.TestCase;
import lia.common.TestUtil;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.junit.Test;

// From chapter 4

public class SynonymAnalyzerTest extends TestCase {

	private IndexSearcher searcher;

	private static SynonymAnalyzer synonymAnalyzer = new SynonymAnalyzer(new TestSynonymEngine());

	@Override
	public void setUp() throws Exception {

		RAMDirectory directory = new RAMDirectory();

		IndexWriter writer = new IndexWriter(directory, synonymAnalyzer, IndexWriter.MaxFieldLength.UNLIMITED);		// #1

		Document doc = new Document();
		doc.add(new Field("content", "The quick brown fox jumps over the lazy dog", Field.Store.YES, Field.Index.ANALYZED));	// #2
		writer.addDocument(doc);
		writer.close();

		searcher = new IndexSearcher(directory, true);
	}

	@Override
	public void tearDown() throws Exception {
		searcher.close();
	}

	@Test
	public void testJumps() throws Exception {

		TokenStream stream = synonymAnalyzer.tokenStream("contents", new StringReader("jumps"));	// #A

		TermAttribute term = stream.addAttribute(TermAttribute.class);
		PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);

		int i = 0;

		String[] expected = new String[] { "jumps", "hops", "leaps" };	// #B

		while (stream.incrementToken()) {

			assertEquals(expected[i], term.term());

			int expectedPos;	// #C

			if (i == 0) {		// #C
				expectedPos = 1;
			} else {
				expectedPos = 0;
			}

			assertEquals(expectedPos, posIncr.getPositionIncrement());	// #C

			i++;
		}

		assertEquals(3, i);
	}

	/*
	 * #A Analyze with SynonymAnalyzer
	 * #B Check for correct synonyms
	 * #C Verify synonyms positions
	 */

	@Test
	public void testSearchByAPI() throws Exception {

		TermQuery tq = new TermQuery(new Term("content", "hops"));	// #1
		assertEquals(1, TestUtil.hitCount(searcher, tq));

		PhraseQuery pq = new PhraseQuery();		// #2
		pq.add(new Term("content", "fox"));		// #2
		pq.add(new Term("content", "hops"));	// #2
		assertEquals(1, TestUtil.hitCount(searcher, pq));
	}

	/*
	 * #1 Search for "hops"
	 * #2 Search for "fox hops"
	 */

	@Test
	public void testWithQueryParser() throws Exception {

		Query query = new QueryParser(Version.LUCENE_30, "content", synonymAnalyzer).parse("\"fox jumps\"");	// 1
		assertEquals(1, TestUtil.hitCount(searcher, query));	// 1

		System.out.println("With SynonymAnalyzer, \"fox jumps\" parses to " + query.toString("content"));

		query = new QueryParser(Version.LUCENE_30, "content", new StandardAnalyzer(Version.LUCENE_30)).parse("\"fox jumps\"");	// B
		assertEquals(1, TestUtil.hitCount(searcher, query));	// 2

		System.out.println("With StandardAnalyzer, \"fox jumps\" parses to " + query.toString("content"));
	}

	/*
	 * #1 SynonymAnalyzer finds the document
	 * #2 StandardAnalyzer also finds document
	 */

	public static void main(String[] args) throws Exception {

		SynonymAnalyzerTest synonymAnalyzerTest = new SynonymAnalyzerTest();

		synonymAnalyzerTest.setUp();
		synonymAnalyzerTest.testJumps();
		synonymAnalyzerTest.testSearchByAPI();
		synonymAnalyzerTest.testWithQueryParser();
	}
}

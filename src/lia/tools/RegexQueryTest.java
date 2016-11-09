package lia.tools;

import junit.framework.TestCase;
import org.apache.lucene.search.regex.RegexQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import lia.common.TestUtil;

// From chapter 8

public class RegexQueryTest extends TestCase {

	public void testRegexQuery() throws Exception {

		Directory directory = TestUtil.getBookIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(directory);

		RegexQuery q = new RegexQuery(new Term("title", ".*st.*"));

		TopDocs hits = searcher.search(q, 10);

		assertEquals(2, hits.totalHits);

		assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Tapestry in Action"));

		assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Mindstorms: Children, Computers, And Powerful Ideas"));

		searcher.close();
		directory.close();
	}
}

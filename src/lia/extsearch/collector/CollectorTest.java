package lia.extsearch.collector;

import junit.framework.TestCase;
import lia.common.TestUtil;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.util.Map;

// From chapter 6

public class CollectorTest extends TestCase {

	public void testCollecting() throws Exception {

		Directory dir = TestUtil.getBookIndexDirectory();

		IndexSearcher searcher = new IndexSearcher(dir);

		TermQuery query = new TermQuery(new Term("contents", "junit"));

		BookLinkCollector collector = new BookLinkCollector();

		searcher.search(query, collector);

		Map<String, String> linkMap = collector.getLinks();

		assertEquals("ant in action", linkMap.get("http://www.manning.com/loughran"));

		TopDocs hits = searcher.search(query, 10);

		TestUtil.dumpHits(searcher, hits);

		searcher.close();
		dir.close();
	}
}

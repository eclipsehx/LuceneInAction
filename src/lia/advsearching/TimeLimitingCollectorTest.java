package lia.advsearching;

import junit.framework.TestCase;
import lia.common.TestUtil;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TimeLimitingCollector;
import org.apache.lucene.search.TimeLimitingCollector.TimeExceededException;

// From chapter 5

public class TimeLimitingCollectorTest extends TestCase {
	
	public void testTimeLimitingCollector() throws Exception {
		
		Directory dir = TestUtil.getBookIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(dir);
		Query q = new MatchAllDocsQuery();
		int numAllBooks = TestUtil.hitCount(searcher, q);

		TopScoreDocCollector topDocs = TopScoreDocCollector.create(10, false);
		Collector collector = new TimeLimitingCollector(topDocs, 1000); // #A ，执行时间太短，改成1都测试出来，中间有一次测试出了
		
		try {
			searcher.search(q, collector);
			assertEquals(numAllBooks, topDocs.getTotalHits()); // #B
		} catch (TimeExceededException tee) { // #C
			System.out.println("Too much time taken."); // #C
		} // #C
		searcher.close();
		dir.close();
	}
}

/*
  #A Wrap any existing Collector
  #B If no timeout, we should have all hits
  #C Timeout hit
*/

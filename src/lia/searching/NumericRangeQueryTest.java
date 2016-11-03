package lia.searching;

import junit.framework.TestCase;
import lia.common.TestUtil;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.store.Directory;

// From chapter 3
public class NumericRangeQueryTest extends TestCase {

	public void testInclusive() throws Exception {

		Directory dir = TestUtil.getBookIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(dir);

		// pub date of TTC was September 2006
		NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("pubmonth", 200605, 200609, true, true);

		TopDocs matches = searcher.search(query, 10);

		for (int i = 0; i < matches.totalHits; i++) {
			System.out.println("match " + i + ": " + searcher.doc(matches.scoreDocs[i].doc).get("pubmonth"));
		}

		assertEquals(1, matches.totalHits);

		searcher.close();
		dir.close();
	}

	public void testExclusive() throws Exception {

		Directory dir = TestUtil.getBookIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(dir);

		// pub date of TTC was September 2006
		NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("pubmonth", 200605, 200609, false, false);

		TopDocs matches = searcher.search(query, 10);

		assertEquals(0, matches.totalHits);

		searcher.close();
		dir.close();
	}
}

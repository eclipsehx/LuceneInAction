package lia.searching;

import junit.framework.TestCase;

import lia.common.TestUtil;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.store.Directory;

// From chapter 3
public class TermRangeQueryTest extends TestCase {

	public void testTermRangeQuery() throws Exception {

		Directory dir = TestUtil.getBookIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(dir);

		TermRangeQuery query = new TermRangeQuery("title2", "d", "j", true, true);

		TopDocs matches = searcher.search(query, 100);

		for (int i = 0; i < matches.totalHits; i++) {
			System.out.println("match " + i + ": " + searcher.doc(matches.scoreDocs[i].doc).get("title2"));
		}

		assertEquals(3, matches.totalHits);

		searcher.close();
		dir.close();
	}
}

package lia.analysis.i18n;

import junit.framework.TestCase;
import lia.common.TestUtil;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.junit.Test;

// From chapter 4

public class ChineseTest extends TestCase {

	@Test
	public void testChinese() throws Exception {

		Directory dir = TestUtil.getBookIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(dir);

		Query query = new TermQuery(new Term("contents", "道"));
		assertEquals("tao", 1, TestUtil.hitCount(searcher, query));
	}
}

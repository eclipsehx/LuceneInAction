package lia.meetlucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

// From chapter 1

/** Just contains any code fragments from chapter 1 */

public class Fragments {

	public void simpleSearch() throws IOException {

		Directory dir = FSDirectory.open(new File("/tmp/index"));
		IndexSearcher searcher = new IndexSearcher(dir);
		Query q = new TermQuery(new Term("contents", "lucene"));
		TopDocs hits = searcher.search(q, 10);
		searcher.close();
	}
}

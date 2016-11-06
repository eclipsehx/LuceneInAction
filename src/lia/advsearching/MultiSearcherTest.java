package lia.advsearching;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import junit.framework.TestCase;

// From chapter 5

public class MultiSearcherTest extends TestCase {
	
	private IndexSearcher[] searchers;
	
	@Override
	public void setUp() throws Exception {
		
		String[] animals = { "aardvark", "beaver", "coati", "dog", "elephant",
				"frog", "gila monster", "horse", "iguana", "javelina",
				"kangaroo", "lemur", "moose", "nematode", "orca", "python",
				"quokka", "rat", "scorpion", "tarantula", "uromastyx",
				"vicuna", "walrus", "xiphias", "yak", "zebra" };

		Analyzer analyzer = new WhitespaceAnalyzer();

		Directory aTOmDirectory = new RAMDirectory(); // #1
		Directory nTOzDirectory = new RAMDirectory(); // #1

		IndexWriter aTOmWriter = new IndexWriter(aTOmDirectory, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
		IndexWriter nTOzWriter = new IndexWriter(nTOzDirectory, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);

		for (int i = animals.length - 1; i >= 0; i--) {
			
			Document doc = new Document();
			String animal = animals[i];
			doc.add(new Field("animal", animal, Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			if (animal.charAt(0) < 'n') {
				aTOmWriter.addDocument(doc); // #2
			} else {
				nTOzWriter.addDocument(doc); // #2
			}
		}

		aTOmWriter.close();
		nTOzWriter.close();

		searchers = new IndexSearcher[2];
		searchers[0] = new IndexSearcher(aTOmDirectory);
		searchers[1] = new IndexSearcher(nTOzDirectory);
	}

	public void testMulti() throws Exception {

		MultiSearcher searcher = new MultiSearcher(searchers);

		TermRangeQuery query = new TermRangeQuery("animal", // #3
				"h", // #3
				"t", // #3
				true, true);// #3

		TopDocs hits = searcher.search(query, 10);
		assertEquals("tarantula not included", 12, hits.totalHits);
	}

	/*
	 * #1 Create two directories
	 * #2 Index halves of the alphabet
	 * #3 Search both indexes
	 */
}

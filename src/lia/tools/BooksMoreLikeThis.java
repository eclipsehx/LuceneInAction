package lia.tools;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.similar.MoreLikeThis;
import org.apache.lucene.index.IndexReader;

import lia.common.TestUtil;

// From chapter 8

public class BooksMoreLikeThis {

	public static void main(String[] args) throws Throwable {

		Directory directory = TestUtil.getBookIndexDirectory();
		IndexReader reader = IndexReader.open(directory);

		IndexSearcher searcher = new IndexSearcher(reader);

		int numDocs = reader.maxDoc();

		MoreLikeThis mlt = new MoreLikeThis(reader);						// #A
		mlt.setFieldNames(new String[] { "title", "author" });
		mlt.setMinTermFreq(1);												// #B
		mlt.setMinDocFreq(1);

		for (int docID = 0; docID < numDocs; docID++) {						// #C

			System.out.println();

			Document doc = reader.document(docID);
			System.out.println(doc.get("title"));

			Query query = mlt.like(docID);									// #D
			System.out.println("  query=" + query);

			TopDocs similarDocs = searcher.search(query, 10);

			if (similarDocs.totalHits == 0) {
				System.out.println("  None like this");
			}

			for (int i = 0; i < similarDocs.scoreDocs.length; i++) {
				if (similarDocs.scoreDocs[i].doc != docID) {				// #E
					doc = reader.document(similarDocs.scoreDocs[i].doc);
					System.out.println("  -> " + doc.getField("title").stringValue());
				}
			}
		}

		searcher.close();
		reader.close();
		directory.close();
	}
}

/*
  #A Instantiate MoreLikeThis
  #B Lower default minimums
  #C Iterate through all docs in the index
  #D Build query to find similar documents
  #E Don't show the same document
*/

package lia.meetlucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

// From chapter 1

/**
 * This code was originally written for Erik's Lucene intro java.net article
 */
public class Searcher {

	public static void main(String[] args) throws IllegalArgumentException, IOException, ParseException {

		if (args.length != 2) {
			throw new IllegalArgumentException("Usage: java " + Searcher.class.getName() + " <index dir> <query>");
		}

		String indexDir = args[0]; 	// 1 Parse provided index directory
		String q = args[1]; 		// 2 Parse provided query string

		search(indexDir, q);
	}

	public static void search(String indexDir, String q) throws IOException, ParseException {

		Directory dir = FSDirectory.open(new File(indexDir)); 	// 3 Open index
		IndexSearcher searcher = new IndexSearcher(dir); 				// 3 Open index

		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30)); // 4 Parse query
		Query query = parser.parse(q); 	// 4

		long start = System.currentTimeMillis();

		TopDocs hits = searcher.search(query, 10); 	// 5 Search index

		long end = System.currentTimeMillis();

		System.err.println("Found " + hits.totalHits + " document(s) (in " + (end - start) + " milliseconds) that matched query '" + q + "':"); // 6 Write search stats

		for (ScoreDoc scoreDoc : hits.scoreDocs) {

			Document doc = searcher.doc(scoreDoc.doc); 		// 7 Retrieve matching document

			System.out.println(doc.get("fullpath")); 	// 8 Display filename
		}

		searcher.close(); 	// 9 Close IndexSearcher
	}
}

/*
#1 Parse provided index directory
#2 Parse provided query string
#3 Open index
#4 Parse query
#5 Search index
#6 Write search stats
#7 Retrieve matching document
#8 Display filename
#9 Close IndexSearcher
*/

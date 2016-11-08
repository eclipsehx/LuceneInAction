package lia.tools.remote;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.TermQuery;

import java.rmi.Naming;
import java.util.Date;
import java.util.HashMap;

// From chapter 9

public class SearchClient {

	private static HashMap<String, MultiSearcher> searcherCache = new HashMap<String, MultiSearcher>();

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.err.println("Usage: java " + SearchClient.class.getName() + " <query>");
			System.exit(-1);
		}

		String word = args[0];

		for (int i = 0; i < 5; i++) {										// 1
			search("LIA_Multi", word);										// 1
			search("LIA_Parallel", word);									// 1
		}
	}

	private static void search(String name, String word) throws Exception {

		TermQuery query = new TermQuery(new Term("word", word));

		MultiSearcher searcher = searcherCache.get(name);											// 2

		if (searcher == null) {
			searcher = new MultiSearcher(new Searchable[] { lookupRemote(name) });					// 3
			searcherCache.put(name, searcher);
		}

		long begin = new Date().getTime();															// 4

		TopDocs hits = searcher.search(query, 10);													// 4

		long end = new Date().getTime();															// 4

		System.out.print("Searched " + name + " for '" + word + "' (" + (end - begin) + " ms): ");

		if (hits.scoreDocs.length == 0) {
			System.out.print("<NONE FOUND>");
		}

		for (ScoreDoc sd : hits.scoreDocs) {

			Document doc = searcher.doc(sd.doc);

			String[] values = doc.getValues("syn");

			for (String syn : values) {
				System.out.print(syn + " ");
			}
		}

		System.out.println();
		System.out.println();																		// 5
	}

	private static Searchable lookupRemote(String name) throws Exception {
		return (Searchable) Naming.lookup("//localhost/" + name);									// 6
	}
}

/*
  #1 Multiple identical searches
  #2 Cache searchers
  #3 Wrap Searchable in MultiSearcher
  #4 Time searching
  #5 Don't close searcher!
  #6 RMI lookup
*/

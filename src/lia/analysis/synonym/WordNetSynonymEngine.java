package lia.analysis.synonym;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lia.common.AllDocCollector;

// From chapter 9

public class WordNetSynonymEngine implements SynonymEngine {

	Directory fsDir;
	IndexSearcher searcher;

	public WordNetSynonymEngine(File index) throws IOException {
		fsDir = FSDirectory.open(index);
		searcher = new IndexSearcher(fsDir);
	}

	public void close() throws IOException {
		searcher.close();
		fsDir.close();
	}

	@Override
	public String[] getSynonyms(String word) throws IOException {

		List<String> synList = new ArrayList<String>();

		AllDocCollector collector = new AllDocCollector();							// #A

		searcher.search(new TermQuery(new Term("word", word)), collector);

		for (ScoreDoc hit : collector.getHits()) {									// #B

			Document doc = searcher.doc(hit.doc);

			String[] values = doc.getValues("syn");

			for (String syn : values) {												// #C
				synList.add(syn);
			}
		}

		return synList.toArray(new String[0]);
	}
}

/*
 * #A Collect every matching document
 * #B Iterate over matching documents
 * #C Record synonyms
 */

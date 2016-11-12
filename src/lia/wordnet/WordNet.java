/**
 * index WordNet database and search for word's synonym
 */
package lia.wordnet;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.wordnet.Syns2Index;
import org.apache.lucene.wordnet.SynLookup;

/**
 * @author eclipsehx
 */
public class WordNet {

	private static final String PROLOG_WN_S_PATH = "./data/prolog/wn_s.pl";
	private static final String WORDNET_INDEX_DIR = "./index/wordnet/";
	private static final String WORDNET_ALPHABET_INDEX_DIR = "./index/wordnet-alphabet/";

	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	private static final String F_SYN = "syn";
	private static final String F_WORD = "word";

	@Test
	public void indexWordNet() throws Throwable {

		String[] args = new String[] { PROLOG_WN_S_PATH, WORDNET_INDEX_DIR };

		Syns2Index.main(args);
	}

	@Test
	public void synLookup() throws IOException {

		String word = "search";

		String[] args = new String[] { WORDNET_INDEX_DIR, word };

		SynLookup.main(args);
	}

	/**
	 * 从 indexWordNet() 建立的索引中读取 doc 内容，重建索引，将 word 按 26 个字母分组建立索引，索引放在以字母命名的目录下
	 * @throws IOException 
	 */
	@Test
	public void indexWordNetAlphabet() throws IOException {

		Directory[] dirs = new Directory[ALPHABET.length()];
		IndexWriter[] writers = new IndexWriter[ALPHABET.length()];
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

		for (int i = 0; i < ALPHABET.length(); i++) {
			dirs[i] = FSDirectory.open(new File(WORDNET_ALPHABET_INDEX_DIR + ALPHABET.charAt(i)));
			writers[i] = new IndexWriter(dirs[i], analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
		}

		Directory dir = FSDirectory.open(new File(WORDNET_INDEX_DIR));	// wordnet 原始索引(总的索引)
		IndexReader reader = IndexReader.open(dir);

		for (int i = 0; i < reader.numDocs(); i++) {

			Document doc = reader.document(i);
			String word = doc.get(F_WORD);

			writers[word.charAt(0) - 'a'].addDocument(doc);
		}

		for (int i = 0; i < ALPHABET.length(); i++) {
			writers[i].optimize();
			writers[i].close();
			dirs[i].close();
		}
	}

	@Test
	public void testWordNetAlphabetIndex() throws IOException {

		Directory[] dirs = new Directory[ALPHABET.length()];
		IndexReader[] readers = new IndexReader[ALPHABET.length()];

		for (int i = 0; i < ALPHABET.length(); i++) {

			System.out.print(ALPHABET.charAt(i));

			dirs[i] = FSDirectory.open(new File(WORDNET_ALPHABET_INDEX_DIR + ALPHABET.charAt(i)));
			readers[i] = IndexReader.open(dirs[i]);

			for (int j = 0; j < readers[i].numDocs(); j++) {
				if (!readers[i].document(j).get(F_WORD).startsWith(ALPHABET.charAt(i) + "")) {
					System.out.println("Error!!!");
				}
			}
		}
	}
}

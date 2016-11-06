package lia.tools.spell;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;

// From chapter 8

public class CreateSpellCheckerIndex {

	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.err.println("Usage: java lia.tools.SpellCheckerTest SpellCheckerIndexDir IndexDir IndexField");
			System.exit(1);
		}

		String spellCheckDir = args[0];
		String indexDir = args[1];
		String indexField = args[2];

		System.out.println("Now build SpellChecker index...");

		Directory dir = FSDirectory.open(new File(spellCheckDir));

		SpellChecker spell = new SpellChecker(dir);							// #A

		long startTime = System.currentTimeMillis();

		Directory dir2 = FSDirectory.open(new File(indexDir));
		IndexReader r = IndexReader.open(dir2);								// #B

		try {
			spell.indexDictionary(new LuceneDictionary(r, indexField));		// #C
		} finally {
			r.close();
		}

		dir.close();
		dir2.close();

		long endTime = System.currentTimeMillis();

		System.out.println("  took " + (endTime - startTime) + " milliseconds");
	}
}

/*
  #A Create SpellChecker on its directory
  #B Open IndexReader containing words to add to spell dictionary
  #C Add all words from the specified fields into the spell checker index
*/

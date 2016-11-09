package lia.tools.BerkeleyDb;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.DatabaseException;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.je.JEDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.util.Version;

// From chapter 9

/**
 * Same as BerkeleyDbIndexer, but uses the Java edition of Berkeley DB
 */
public class BerkeleyDbJEIndexer {

	public static void main(String[] args) throws IOException, DatabaseException {

		if (args.length != 1) {
			System.err.println("Usage: BerkeleyDbIndexer <index dir>");
			System.exit(-1);
		}

		File indexFile = new File(args[0]);

		if (indexFile.exists()) {											// A

			File[] files = indexFile.listFiles();							// A

			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().startsWith("__"))					// A
					files[i].delete();										// A
			}

			indexFile.delete();												// A
		}

		indexFile.mkdir();

		EnvironmentConfig envConfig = new EnvironmentConfig();				// B
		DatabaseConfig dbConfig = new DatabaseConfig();						// B

		envConfig.setTransactional(true);									// B
		envConfig.setAllowCreate(true);										// B

		dbConfig.setTransactional(true);									// B
		dbConfig.setAllowCreate(true);										// B

		Environment env = new Environment(indexFile, envConfig);			// C

		Transaction txn = env.beginTransaction(null, null);					// C
		Database index = env.openDatabase(txn, "__index__", dbConfig);		// C
		Database blocks = env.openDatabase(txn, "__blocks__", dbConfig); 	// C
		txn.commit();														// C
		txn = env.beginTransaction(null, null);								// C

		JEDirectory directory = new JEDirectory(txn, index, blocks);		// D

		IndexWriter writer = new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_30), true, IndexWriter.MaxFieldLength.UNLIMITED);

		Document doc = new Document();
		doc.add(new Field("contents", "The quick brown fox...", Field.Store.YES, Field.Index.ANALYZED));

		writer.addDocument(doc);

		writer.optimize();
		writer.close();

		directory.close();
		txn.commit();

		index.close();
		blocks.close();
		env.close();

		System.out.println("Indexing Complete");
	}
}

/*
  #A Remove existing index, if present
  #B Configure BDB's environment and database
  #C Open database and transaction
  #D Create JEDirectory
*/

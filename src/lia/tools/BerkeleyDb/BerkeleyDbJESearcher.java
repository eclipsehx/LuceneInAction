package lia.tools.BerkeleyDb;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.je.JEDirectory;

import java.io.File;
import java.io.IOException;

// From chapter 9

public class BerkeleyDbJESearcher {

	public static void main(String[] args) throws IOException, DatabaseException {

		if (args.length != 1) {
			System.err.println("Usage: BerkeleyDbSearcher <index dir>");
			System.exit(-1);
		}

		File indexFile = new File(args[0]);

		EnvironmentConfig envConfig = new EnvironmentConfig();
		DatabaseConfig dbConfig = new DatabaseConfig();

		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);

		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(true);

		Environment env = new Environment(indexFile, envConfig);

		Database index = env.openDatabase(null, "__index__", dbConfig);
		Database blocks = env.openDatabase(null, "__blocks__", dbConfig);

		JEDirectory directory = new JEDirectory(null, index, blocks);

		IndexSearcher searcher = new IndexSearcher(directory, true);
		TopDocs hits = searcher.search(new TermQuery(new Term("contents", "fox")), 10);

		System.out.println(hits.totalHits + " document(s) found");

		searcher.close();

		index.close();
		blocks.close();
		env.close();
	}
}

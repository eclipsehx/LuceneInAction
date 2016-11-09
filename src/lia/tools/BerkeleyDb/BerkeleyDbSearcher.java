package lia.tools.BerkeleyDb;

import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.db.DbDirectory;

import java.io.File;
import java.io.IOException;

// From chapter 9

public class BerkeleyDbSearcher {

	public static void main(String[] args) throws IOException, DatabaseException {

		if (args.length != 1) {
			System.err.println("Usage: BerkeleyDbSearcher <index dir>");
			System.exit(-1);
		}

		File indexFile = new File(args[0]);

		EnvironmentConfig envConfig = new EnvironmentConfig();
		DatabaseConfig dbConfig = new DatabaseConfig();

		envConfig.setTransactional(true);
		envConfig.setInitializeCache(true);
		envConfig.setInitializeLocking(true);
		envConfig.setInitializeLogging(true);
		envConfig.setAllowCreate(true);
		envConfig.setThreaded(true);

		dbConfig.setAllowCreate(true);
		dbConfig.setType(DatabaseType.BTREE);

		Environment env = new Environment(indexFile, envConfig);

		Database index = env.openDatabase(null, "__index__", null, dbConfig);
		Database blocks = env.openDatabase(null, "__blocks__", null, dbConfig);

		DbDirectory directory = new DbDirectory(null, index, blocks, 0);

		IndexSearcher searcher = new IndexSearcher(directory, true);
		TopDocs hits = searcher.search(new TermQuery(new Term("contents", "fox")), 10);

		System.out.println(hits.totalHits + " document(s) found");

		searcher.close();

		index.close();
		blocks.close();
		env.close();
	}
}

package lia.tools.remote;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.RemoteSearchable;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

// From chapter 9

public class SearchServer {

	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.err.println("Usage: java " + SearchServer.class.getName() + " <basedir>");
			System.exit(-1);
		}

		String basedir = args[0];																	// 1

		Directory[] dirs = new Directory[ALPHABET.length()];
		Searchable[] searchables = new Searchable[ALPHABET.length()];

		for (int i = 0; i < ALPHABET.length(); i++) {
			dirs[i] = FSDirectory.open(new File(basedir, "" + ALPHABET.charAt(i)));
			searchables[i] = new IndexSearcher(dirs[i]);											// 2
		}

		LocateRegistry.createRegistry(1099);														// 3

		Searcher multiSearcher = new MultiSearcher(searchables);									// 4
		RemoteSearchable multiImpl = new RemoteSearchable(multiSearcher);							// 4
		Naming.rebind("//localhost/LIA_Multi", multiImpl);											// 4

		Searcher parallelSearcher = new ParallelMultiSearcher(searchables);							// 5
		RemoteSearchable parallelImpl = new RemoteSearchable(parallelSearcher);						// 5
		Naming.rebind("//localhost/LIA_Parallel", parallelImpl);									// 5

		System.out.println("Server started ......");

		for (int i = 0; i < ALPHABET.length(); i++) {
			dirs[i].close();
		}
	}
}

/*
  #1 Indexes under basedir
  #2 Open IndexSearcher for each index
  #3 Create RMI registry
  #4 MultiSearcher over all indexes
  #5 ParallelMultiSearcher over all indexes
*/

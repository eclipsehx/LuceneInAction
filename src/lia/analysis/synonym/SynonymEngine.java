package lia.analysis.synonym;

import java.io.IOException;

// From chapter 4

public interface SynonymEngine {
	
	String[] getSynonyms(String s) throws IOException;
}

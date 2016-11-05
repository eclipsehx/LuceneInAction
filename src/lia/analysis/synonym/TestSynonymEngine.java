package lia.analysis.synonym;

import java.util.HashMap;

// From chapter 4

public class TestSynonymEngine implements SynonymEngine {

	private static HashMap<String, String[]> map = new HashMap<String, String[]>();

	static {
		map.put("quick", new String[] { "fast", "speedy" });
		map.put("jumps", new String[] { "leaps", "hops" });
		map.put("over", new String[] { "above" });
		map.put("lazy", new String[] { "apathetic", "sluggish" });
		map.put("dog", new String[] { "canine", "pooch" });
	}

	@Override
	public String[] getSynonyms(String s) {
		return map.get(s);
	}
}

package lia.advsearching;

import org.apache.lucene.search.*;

// From chapter 5

public class Fragments {

	public void frags1() throws Exception {
		String jan1 = null;
		String jan31 = null;
		String modified = null;
		Filter filter;
		// START
		filter = new TermRangeFilter(modified, null, jan31, false, true);
		filter = new TermRangeFilter(modified, jan1, null, true, false);
		filter = TermRangeFilter.Less(modified, jan31);
		filter = TermRangeFilter.More(modified, jan1);
		// END
	}
}
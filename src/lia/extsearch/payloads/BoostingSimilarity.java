package lia.extsearch.payloads;

import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.analysis.payloads.PayloadHelper;

// From chapter 6

public class BoostingSimilarity extends DefaultSimilarity {

	@Override
	public float scorePayload(int docID, String fieldName, int start, int end, byte[] payload, int offset, int length) {

		if (payload != null) {
			return PayloadHelper.decodeFloat(payload, offset);
		} else {
			return 1.0F;
		}
	}
}

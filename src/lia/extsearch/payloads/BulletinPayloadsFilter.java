package lia.extsearch.payloads;

import java.io.IOException;

import org.apache.lucene.index.Payload;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.payloads.PayloadHelper;

// From chapter 6

public class BulletinPayloadsFilter extends TokenFilter {

	private TermAttribute termAtt;
	private PayloadAttribute payloadAttr;
	private Payload boostPayload;
	private boolean isBulletin;

	BulletinPayloadsFilter(TokenStream in, float warningBoost) {
		super(in);
		termAtt = addAttribute(TermAttribute.class);
		payloadAttr = addAttribute(PayloadAttribute.class);
		boostPayload = new Payload(PayloadHelper.encodeFloat(warningBoost));
	}

	void setIsBulletin(boolean v) {
		isBulletin = v;
	}

	@Override
	public final boolean incrementToken() throws IOException {

		if (input.incrementToken()) {

			if (isBulletin && termAtt.term().equals("warning")) {	// #A
				payloadAttr.setPayload(boostPayload);	// #A
			} else {
				payloadAttr.setPayload(null);	// #B
			}

			return true;

		} else {
			return false;
		}
	}
}

/*
 * #A Add payload boost
 * #B Clear payload
 */

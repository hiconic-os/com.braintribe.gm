package com.braintribe.gm.graphfetching.processing.query;

import java.util.List;

import com.braintribe.gm.graphfetching.api.query.FetchResults;
import com.braintribe.model.record.ListRecord;

public class GmSessionFetchResults implements FetchResults {
	private final List<ListRecord> rows;
	private final int size;
	private int i = 0;
	private ListRecord row;
	
	public GmSessionFetchResults(List<ListRecord> rows) {
		this.rows = rows;
		this.size = rows.size();
	}

	@Override
	public boolean next() {
		if (i < size) {
			row = rows.get(i);
			i++;
			return true;
		}
		else {
			row = null;
			return false;
		}
	}

	@Override
	public <V> V get(int col) {
		if (row == null)
			throw new IllegalStateException("Either you miss a next() call or you at the end of the results");
		
		return (V) row.get(col);
	}
	
	@Override
	public void close() {
		// nothing do do here;
	}
}

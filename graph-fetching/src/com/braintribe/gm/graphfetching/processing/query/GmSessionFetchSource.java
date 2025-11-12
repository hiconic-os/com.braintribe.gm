package com.braintribe.gm.graphfetching.processing.query;

import com.braintribe.gm.graphfetching.api.query.FetchJoin;
import com.braintribe.gm.graphfetching.api.query.FetchSource;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.JoinType;
import com.braintribe.model.query.Source;

public class GmSessionFetchSource implements FetchSource {
	protected final GmSessionFetchQuery query;
	protected final Source source;
	private final int pos;
	
	public GmSessionFetchSource(GmSessionFetchQuery query, Source source, int pos) {
		super();
		this.query = query;
		this.source = source;
		this.pos = pos;
	}

	@Override
	public int pos() {
		return pos;
	}

	@Override
	public FetchJoin leftJoin(Property property) {
		return join(property, JoinType.left);
	}

	@Override
	public FetchJoin join(Property property) {
		return join(property, JoinType.inner);
	}
	
	private FetchJoin join(Property property, JoinType joinType) {
		String propertyName = property.getName();
		Join join = source.join(propertyName, joinType);
		int pos = query.select(join);
		return new GmSessionFetchJoin(property, query, join, pos); 
	}
}

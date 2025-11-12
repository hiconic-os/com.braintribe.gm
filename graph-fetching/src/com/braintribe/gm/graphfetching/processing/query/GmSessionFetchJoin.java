package com.braintribe.gm.graphfetching.processing.query;

import com.braintribe.gm.graphfetching.api.query.FetchJoin;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.functions.ListIndex;

public class GmSessionFetchJoin extends GmSessionFetchSource implements FetchJoin {
	
	private Property property;

	public GmSessionFetchJoin(Property property, GmSessionFetchQuery query, Join source, int pos) {
		super(query, source, pos);
		this.property = property;
	}

	@Override
	public void orderByIfRequired() {
		if (property.getType().getTypeCode() == TypeCode.listType) {
			ListIndex listIndex = ListIndex.T.create();
			listIndex.setJoin((Join)source);
			query.gmQuery().orderBy(listIndex);
		}			
	}
}

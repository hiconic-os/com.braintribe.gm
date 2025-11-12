package com.braintribe.gm.graphfetching.processing.query;

import com.braintribe.gm.graphfetching.api.query.FetchQuery;
import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class GmSessionFetchQueryFactory implements FetchQueryFactory {
	private PersistenceGmSession session;

	public GmSessionFetchQueryFactory(PersistenceGmSession session) {
		super();
		this.session = session;
	}
	
	@Override
	public FetchQuery createQuery(EntityType<?> entityType) {
		return new GmSessionFetchQuery(session, entityType);
	}
}

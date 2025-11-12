package com.braintribe.gm.graphfetching.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.braintribe.gm.graphfetching.api.FetchBuilder;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.gm.graphfetching.processing.fetch.FetchProcessing;
import com.braintribe.gm.graphfetching.processing.util.FetchingTools;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class FetchBuilderImpl implements FetchBuilder {
	private PersistenceGmSession session;
	private EntityGraphNode node;
	private FetchQueryFactory queryFactory;
	
	public FetchBuilderImpl(PersistenceGmSession session, EntityGraphNode node) {
		super();
		this.session = session;
		this.node = node;
	}
	@Override
	public FetchBuilder queryFactory(FetchQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
		return this;
	}
	
	@Override
	public <E extends GenericEntity> List<E> fetchDetached(Collection<? extends E> entities) {
		List<E> detachedEntities = new ArrayList<>();

		for (E entity : entities) {
			E detachedEntity = FetchingTools.cloneDetachment(entity);
			detachedEntities.add(detachedEntity);
		}

		try (FetchProcessing processing = fetchProcessing()) {
			processing.fetch(node, detachedEntities);

			return detachedEntities;
		}
	}
	
	@Override
	public void fetch(Collection<? extends GenericEntity> entities) {
		if (entities.isEmpty())
			return;

		GenericEntity entity = entities.iterator().next();

		if (entity.session() == session) {
			List<GenericEntity> detachedEntities = fetchDetached(entities);
			session.merge().adoptUnexposed(true).suspendHistory(true).doFor(detachedEntities);
		} else {
			try (FetchProcessing processing = fetchProcessing()) {
				processing.fetch(node, entities);
			}
		}
	}
	
	private FetchProcessing fetchProcessing() {
		if (queryFactory != null)
			return new FetchProcessing(session, queryFactory);
		else
			return new FetchProcessing(session);
			
	}
}

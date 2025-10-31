package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.braintribe.common.lcd.Pair;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * Implements the orchestration ('processing') for deep entity fetches according to a fetch graph (EntityGraphNode trees).
 * Core idea: Split fetches into tasks (to-one, to-many), schedule/queue them, and process recursively in breadth/depth.
 * Extensible via FetchTask and FetchType.
 */
public class FetchProcessing implements FetchContext {
	private Queue<FetchTask> taskQueue = new LinkedList<>();
	
	private PersistenceGmSession session;
	private Map<Pair<EntityType<?>, Object>, EntityIdm> index = new HashMap<>();
	
	public FetchProcessing(PersistenceGmSession session) {
		this.session = session;
	}
	
	public EntityIdm acquireEntity(GenericEntity entity) {
		return index.computeIfAbsent(Pair.of(entity.entityType(), entity.getId()), k -> new EntityIdm(entity));
	}
	
	public EntityIdm resolveEntity(EntityType<?> type, Object id) {
		return index.get(Pair.of(type, id));
	}
	
	public PersistenceGmSession session() {
		return session;
	}
	
	/**
	 * Entry point for regular fetch: wraps entity list as indexed map and processes.
	 */
	public void fetch(EntityGraphNode node, Collection<? extends GenericEntity> entities) {
		fetch(node, entityIndex(entities));
	}
	
	/**
	 * Main scheduling: enqueue required fetch tasks for the given graph node and entities and process all.
	 */
	public void fetch(EntityGraphNode node, Map<Object, GenericEntity> entities) {
		FetchQualification fqToOne = new FetchQualification(node, FetchType.TO_ONE);
		FetchQualification fqToMany = new FetchQualification(node, FetchType.TO_MANY);
		for (GenericEntity entity : entities.values()) {
			EntityIdm idm = acquireEntity(entity);
			idm.addHandled(fqToOne);
			idm.addHandled(fqToMany);
		}
		enqueueToOneIfRequired(node, entities);
		enqueueToManyIfRequired(node, entities);
		process();
	}
	
	private Map<Object, GenericEntity> entityIndex(Collection<? extends GenericEntity> entities) {
		Map<Object, GenericEntity> index = new LinkedHashMap<Object, GenericEntity>();
		for (GenericEntity entity: entities) {
			index.put(entity.getId(), entity);
		}
		return index;
	}

	public void enqueueToOneIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities) {
		if (node.hasEntityProperties() && !entities.isEmpty())
			taskQueue.offer(new FetchTask(node, FetchType.TO_ONE, entities));
	}

	public void enqueueToManyIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities) {
		if (node.hasCollectionProperties() && !entities.isEmpty())
			taskQueue.offer(new FetchTask(node, FetchType.TO_MANY, entities));
	}
	
	private void process() {
		while (true) {
			FetchTask task = taskQueue.poll();
			
			if (task == null) 
				break;

			processTask(task);
		}
	}
	
	/**
	 * Task worker: decide if a TO_ONE or TO_MANY fetch is required, and process accordingly.
	 */
	private void processTask(FetchTask task) {
		switch (task.fetchType) {
		case TO_MANY: processToManyTask(task); break;
		case TO_ONE: processToOneTask(task); break;
		}
	}

	/**
	 * Handles deep to-one fetching with follow-up to-many fetches on descendants.
	 */
	private void processToOneTask(FetchTask task) {
		ToOneRecursiveFetching toOneFetching = new ToOneRecursiveFetching(task.node);
		toOneFetching.fetch(this, task);
	}

	/**
	 * Handles deep to-many fetching with possible further graph traversal.
	 */
	private void processToManyTask(FetchTask task) {
		ToManyFetching.fetch(this, task.node, task);
	}
}

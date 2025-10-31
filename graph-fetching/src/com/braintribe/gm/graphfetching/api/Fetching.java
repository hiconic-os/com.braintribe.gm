package com.braintribe.gm.graphfetching.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.UntypedGraphNode;
import com.braintribe.gm.graphfetching.processing.fetch.FetchProcessing;
import com.braintribe.gm.graphfetching.processing.fetch.LocalFetching;
import com.braintribe.gm.graphfetching.processing.node.ConfigurableEntityGraphNode;
import com.braintribe.gm.graphfetching.processing.node.ConfigurableUntypedGraphNode;
import com.braintribe.gm.graphfetching.processing.node.GraphPrototypePai;
import com.braintribe.gm.graphfetching.processing.node.ReachableNodeCollector;
import com.braintribe.gm.graphfetching.processing.util.FetchingTools;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * Provides a framework for efficient deep fetching of entity object graphs from a PersistenceGmSession.
 * <p>
 * Fetching allows two intuitive ways to define which parts of an object graph should be loaded:
 * <ul>
 * <li>Using type-safe prototype entity instances: Properties accessed via getters become 'active' for fetch.</li>
 * <li>Using an untyped node tree (rootNode + node): Allows dynamic definition with automatic type inference.</li>
 * </ul>
 *
 * <b>Example usage (typed):</b>
 * 
 * <pre>
 *   MyType prototype = Fetching.selectPrototype(MyType.T);
 *   prototype.getReferenceEntity().getSomeDeepProperty();
 *   List<MyType> entities = ...;
 *   EntityGraphNode fetchPlan = Fetching.rootNode(prototype);
 *   Fetching.fetch(session, fetchPlan, entities);
 * </pre>
 *
 * <b>Example usage (untyped):</b>
 * 
 * <pre>
 * EntityGraphNode fetchPlan = Fetching.rootNode(MyType.T, Fetching.node("referenceEntity", Fetching.node("someDeepProperty")));
 * Fetching.fetch(session, fetchPlan, entities);
 * </pre>
 */
public interface Fetching {
	/**
	 * Creates a prototype instance for the given entity type, suitable for specifying a fetch graph via property access. All properties accessed on
	 * this instance will become part of the implicit fetch plan.
	 * 
	 * @param type
	 *            the entity type
	 * @return a prototype instance
	 */
	static <E extends GenericEntity> E graphPrototype(EntityType<E> type) {
		return type.create(GraphPrototypePai.INSTANCE);
	}

	/**
	 * Creates an untyped graph node for use in fetch graph definitions.
	 * 
	 * @param name
	 *            the name of the property/node
	 * @param subNodes
	 *            optional subnodes (for nested fetches)
	 * @return a new UntypedGraphNode
	 */
	static UntypedGraphNode node(String name, UntypedGraphNode... subNodes) {
		return new ConfigurableUntypedGraphNode(name, subNodes);
	}

	/**
	 * Creates a root node for the fetch graph, starting from the given entity type.
	 * 
	 * @param entityType
	 *            the root entity's type
	 * @param subNodes
	 *            optional subnodes (property branches)
	 * @return a new EntityGraphNode representing the root of the fetch graph
	 */
	static EntityGraphNode rootNode(EntityType<?> entityType, UntypedGraphNode... subNodes) {
		return new ConfigurableEntityGraphNode(entityType, subNodes);
	}

	/**
	 * Creates a node with transitive subnodes for the complete reachable structure but stops at type recurrence
	 * 
	 * @param entityType
	 *            the root entity's type
	 * @return a new EntityGraphNode representing the root of the fetch graph
	 */
	static EntityGraphNode reachable(EntityType<?> entityType) {
		return ReachableNodeCollector.collect(entityType);
	}

	/**
	 * Creates a root node for the fetch graph from a prototype instance.
	 * 
	 * @param selectPrototype
	 *            prototype instance created by {@link #graphPrototype(EntityType)}
	 * @return a new EntityGraphNode representing the root of the fetch graph
	 */
	static EntityGraphNode rootNode(GenericEntity selectPrototype) {
		return GraphPrototypePai.convert(selectPrototype);
	}

	static <E extends GenericEntity> List<E> fetchFromLocal(EntityGraphNode node, Collection<? extends E> entities) {
		@SuppressWarnings("unchecked")
		List<E> fetched = (List<E>) new LocalFetching().fetch(node, entities);
		return fetched;
	}

	/**
	 * Deep-fetches the specified entities according to the given graph node, returning newly detached instances.
	 * 
	 * @param session
	 *            the PersistenceGmSession (detached mode)
	 * @param node
	 *            the fetch graph definition (root)
	 * @param entities
	 *            the entities to fetch
	 * @return detached, deep-fetched entities
	 */
	static <E extends GenericEntity> List<E> fetchDetached(PersistenceGmSession session, EntityGraphNode node, Collection<? extends E> entities) {
		List<E> detachedEntities = new ArrayList<>();

		for (E entity : entities) {
			E detachedEntity = FetchingTools.cloneDetachment(entity);
			detachedEntities.add(detachedEntity);
		}

		FetchProcessing processing = new FetchProcessing(session);
		processing.fetch(node, detachedEntities);

		return detachedEntities;
	}

	/**
	 * Populates the entity graph for the given entities within the specified session. Entities must belong to the session or be detached. Handles
	 * both attached and detached scenarios
	 * 
	 * @param session
	 *            PersistenceGmSession
	 * @param node
	 *            The graph of properties/entities to fetch (created via rootNode/Prototypes)
	 * @param entities
	 *            The root entities to populate
	 */
	static void fetch(PersistenceGmSession session, EntityGraphNode node, Collection<? extends GenericEntity> entities) {

		if (entities.isEmpty())
			return;

		GenericEntity entity = entities.iterator().next();

		if (entity.session() == session) {
			List<GenericEntity> detachedEntities = fetchDetached(session, node, entities);
			session.merge().adoptUnexposed(true).suspendHistory(true).doFor(detachedEntities);
		} else {
			FetchProcessing processing = new FetchProcessing(session);
			processing.fetch(node, entities);
		}
	}
}
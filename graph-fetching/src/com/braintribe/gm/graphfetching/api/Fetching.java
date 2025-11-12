package com.braintribe.gm.graphfetching.api;

import java.util.Collection;
import java.util.List;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.gm.graphfetching.api.node.ReachableNodeBuilder;
import com.braintribe.gm.graphfetching.processing.FetchBuilderImpl;
import com.braintribe.gm.graphfetching.processing.fetch.LocalFetching;
import com.braintribe.gm.graphfetching.processing.node.ConfigurableEntityGraphNode;
import com.braintribe.gm.graphfetching.processing.node.ConfigurableInferableGraphNode;
import com.braintribe.gm.graphfetching.processing.node.GraphPrototypePai;
import com.braintribe.gm.graphfetching.processing.node.ReachableNodeCollector;
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
	static InferableGraphNode node(String name, InferableGraphNode... subNodes) {
		return new ConfigurableInferableGraphNode(name, subNodes);
	}

	static InferableGraphNode node(String name, EntityType<?> entityType, InferableGraphNode... subNodes) {
		return new ConfigurableInferableGraphNode(entityType, name, subNodes);
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
	static EntityGraphNode rootNode(EntityType<?> entityType, InferableGraphNode... subNodes) {
		return new ConfigurableEntityGraphNode(entityType, subNodes);
	}

	static ReachableNodeBuilder reachable(EntityType<?> entityType) {
		return ReachableNodeCollector.builder(entityType);
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
		return build(session, node).fetchDetached(entities);
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
		build(session, node).fetch(entities);
	}
	
	static FetchBuilder build(PersistenceGmSession session, EntityGraphNode node) {
		return new FetchBuilderImpl(session, node);
	}
}
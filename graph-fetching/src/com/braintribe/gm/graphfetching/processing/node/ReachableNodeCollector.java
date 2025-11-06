package com.braintribe.gm.graphfetching.processing.node;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ReachableNodeBuilder;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

public class ReachableNodeCollector {

	public static ReachableNodeBuilder builder(EntityType<?> entityType) {
		return new CollectContext(entityType);
	}

	private static class CollectContext implements ReachableNodeBuilder {
		Function<EntityType<?>, Collection<? extends EntityType<?>>> covariance = t -> Collections.emptyList();
		EntityType<?> entityType;
		Predicate<EntityType<?>> typeExclusion = t -> false;
		Set<EntityType<?>> entityTypeStack = new HashSet<>();

		public CollectContext(EntityType<?> entityType) {
			this.entityType = entityType;
		}

		@Override
		public ReachableNodeBuilder covariance(Function<EntityType<?>, Collection<? extends EntityType<?>>> covariance) {
			this.covariance = covariance;
			return this;
		}
		@Override
		public ReachableNodeBuilder covariance(ModelOracle modelOracle) {
			this.covariance = t -> modelOracle.getEntityTypeOracle(t).getSubTypes().transitive().onlyInstantiable().asTypes();
			return this;
		}
		@Override
		public ReachableNodeBuilder typeExclusion(Predicate<EntityType<?>> typeExclusion) {
			this.typeExclusion = typeExclusion;
			return this;
		}

		@Override
		public EntityGraphNode build() {
			return collect(this);
		}
	}

	public static EntityGraphNode collect(CollectContext context) {

		ConfigurableEntityGraphNode configurableEntityGraphNode = new ConfigurableEntityGraphNode(context.entityType);

		fillProperties(context, context.entityType, context.entityType, configurableEntityGraphNode);

		return configurableEntityGraphNode;
	}

	private static void fillProperties(CollectContext context, EntityType<?> baseType, EntityType<?> entityType,
			ConfigurableEntityGraphNode configurableEntityGraphNode) {

		if (!context.entityTypeStack.add(entityType)) {
			return;
		}
		try {
			Collection<Property> properties = getProperties(baseType, entityType);
			for (Property property : properties) {
				GenericModelType propertyType = property.getType();
				if (propertyType.isScalar() || property.isIdentifier()) {
					continue;
				}

				if (propertyType.isEntity()) {
					EntityType<?> propertyEntityType = (EntityType<?>) propertyType;
					if (context.typeExclusion.test(propertyEntityType)) {
						continue;
					}

					configurableEntityGraphNode.add(entityPropertyGraphNode(context, propertyEntityType, propertyEntityType, property));

					Collection<? extends EntityType<?>> covariantTypes = context.covariance.apply(propertyEntityType);

					for (EntityType<?> covariantType : covariantTypes) {
						configurableEntityGraphNode.add(entityPropertyGraphNode(context, propertyEntityType, covariantType, property));
					}

				} else if (propertyType.isCollection()) {

					CollectionType collectionType = (CollectionType) propertyType;

					switch (collectionType.getCollectionKind()) {
						case list:
						case set:
							LinearCollectionType linearCollectionType = (LinearCollectionType) collectionType;
							GenericModelType elementType = linearCollectionType.getCollectionElementType();
							if (elementType.isEntity()) {
								EntityType<?> elementEntityType = (EntityType<?>) elementType;
								if (context.typeExclusion.test(elementEntityType)) {
									continue;
								}

								configurableEntityGraphNode
										.add(entityCollectionPropertyGraphNode(context, elementEntityType, elementEntityType, property));

								Collection<? extends EntityType<?>> covariantTypes = context.covariance.apply(elementEntityType);

								for (EntityType<?> covariantType : covariantTypes) {
									configurableEntityGraphNode
											.add(entityCollectionPropertyGraphNode(context, elementEntityType, covariantType, property));
								}

							} else {
								configurableEntityGraphNode.add(new ConfigurableScalarCollectionPropertyGraphNode(property));
							}
							break;
						case map:
							break;
						default:
							break;

					}

				}

			}
		} finally {
			context.entityTypeStack.remove(entityType);
		}
	}

	private static Collection<Property> getProperties(EntityType<?> baseType, EntityType<?> entityType) {
		if (baseType == entityType)
			return entityType.getProperties();

		Set<Property> covariantProperties = new LinkedHashSet<Property>(entityType.getProperties());
		covariantProperties.removeAll(baseType.getProperties());
		return covariantProperties;
	}

	private static EntityPropertyGraphNode entityPropertyGraphNode(CollectContext context, EntityType<?> baseType, EntityType<?> entityType,
			Property property) {

		ConfigurableEntityPropertyGraphNode configurableEntityGraphNode = new ConfigurableEntityPropertyGraphNode(property, entityType);

		fillProperties(context, baseType, entityType, configurableEntityGraphNode);

		return configurableEntityGraphNode;
	}

	private static EntityCollectionPropertyGraphNode entityCollectionPropertyGraphNode(CollectContext context, EntityType<?> baseType,
			EntityType<?> entityType, Property property) {

		ConfigurableEntityCollectionPropertyGraphNode configurableEntityGraphNode = new ConfigurableEntityCollectionPropertyGraphNode(property,
				entityType);

		fillProperties(context, baseType, entityType, configurableEntityGraphNode);

		return configurableEntityGraphNode;
	}

}

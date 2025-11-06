package com.braintribe.gm.graphfetching.processing.node;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

public class ReachableNodeCollector {
	
	public static EntityGraphNode collect(ModelOracle oracle, EntityType<?> entityType) {
		
		return collect(
				t -> oracle.getEntityTypeOracle(t).getSubTypes().onlyInstantiable().asTypes(),
				entityType
		);
	}

	public static EntityGraphNode collect(EntityType<?> entityType) {
		return collect(t -> Collections.singletonList(t), entityType);
	}
	
	public static EntityGraphNode collect(Function<EntityType<?>, Collection<? extends EntityType<?>>> covariance, EntityType<?> entityType) {
		
		ConfigurableEntityGraphNode configurableEntityGraphNode = new ConfigurableEntityGraphNode(entityType);
		
		Set<EntityType<?>> entityTypeStack = new HashSet<>();
		
		fillProperties(covariance, entityType, entityType, configurableEntityGraphNode, entityTypeStack);
		
		return configurableEntityGraphNode;
	}

	private static void fillProperties(Function<EntityType<?>, Collection<? extends EntityType<?>>> covariance, EntityType<?> baseType, EntityType<?> entityType, ConfigurableEntityGraphNode configurableEntityGraphNode,
			Set<EntityType<?>> entityTypeStack) {

		if (!entityTypeStack.add(entityType)) {
			return;
		}
		try {
			for (Property property : getProperties(baseType, entityType)) {
				GenericModelType propertyType = property.getType();
				if (propertyType.isScalar() || property.isIdentifier()) {
					continue;
				}

				if (propertyType.isEntity()) {
					EntityType<?> propertyEntityType = (EntityType<?>) propertyType;
					
					configurableEntityGraphNode.add(entityPropertyGraphNode(covariance, propertyEntityType, propertyEntityType, property, entityTypeStack));

					Collection<? extends EntityType<?>> covariantTypes = covariance.apply(propertyEntityType);
					
					for (EntityType<?> covariantType: covariantTypes) {
						configurableEntityGraphNode.add(entityPropertyGraphNode(covariance, propertyEntityType, covariantType, property, entityTypeStack));
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
								configurableEntityGraphNode.add(entityCollectionPropertyGraphNode(covariance, elementEntityType, elementEntityType, property, entityTypeStack));

								Collection<? extends EntityType<?>> covariantTypes = covariance.apply(elementEntityType);
								
								for (EntityType<?> covariantType: covariantTypes) {
									configurableEntityGraphNode.add(entityCollectionPropertyGraphNode(covariance, elementEntityType, covariantType, property, entityTypeStack));
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
			entityTypeStack.remove(entityType);
		}
	}

	private static Collection<Property> getProperties(EntityType<?> baseType, EntityType<?> entityType) {
		if (baseType == entityType)
			return entityType.getProperties();
		
		Set<Property> covariantProperties = new LinkedHashSet<Property>(entityType.getProperties());
		covariantProperties.removeAll(baseType.getProperties());
		return covariantProperties;
	}

	private static EntityPropertyGraphNode entityPropertyGraphNode(Function<EntityType<?>, Collection<? extends EntityType<?>>> covariance, EntityType<?> baseType, EntityType<?> entityType, Property property, Set<EntityType<?>> entityTypeStack) {

		ConfigurableEntityPropertyGraphNode configurableEntityGraphNode = new ConfigurableEntityPropertyGraphNode(property, entityType);

		fillProperties(covariance, baseType, entityType, configurableEntityGraphNode, entityTypeStack);

		return configurableEntityGraphNode;
	}

	private static EntityCollectionPropertyGraphNode entityCollectionPropertyGraphNode(Function<EntityType<?>, Collection<? extends EntityType<?>>> covariance, EntityType<?> baseType, EntityType<?> entityType, Property property,
			Set<EntityType<?>> entityTypeStack) {

		ConfigurableEntityCollectionPropertyGraphNode configurableEntityGraphNode = new ConfigurableEntityCollectionPropertyGraphNode(property,
				entityType);

		fillProperties(covariance, baseType, entityType, configurableEntityGraphNode, entityTypeStack);

		return configurableEntityGraphNode;
	}

}

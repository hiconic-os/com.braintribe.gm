package com.braintribe.gm.graphfetching.processing.node;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;

public class ReachableNodeCollector {

	public static EntityGraphNode collect(EntityType<?> entityType) {

		ConfigurableEntityGraphNode configurableEntityGraphNode = new ConfigurableEntityGraphNode(entityType);

		Set<EntityType<?>> entityTypeStack = new HashSet<>();

		fillProperties(entityType, configurableEntityGraphNode, entityTypeStack);

		return configurableEntityGraphNode;
	}

	private static void fillProperties(EntityType<?> entityType, ConfigurableEntityGraphNode configurableEntityGraphNode,
			Set<EntityType<?>> entityTypeStack) {

		if (!entityTypeStack.add(entityType)) {
			return;
		}
		try {
			for (Property property : entityType.getProperties()) {
				GenericModelType propertyType = property.getType();
				if (propertyType.isScalar() || property.isIdentifier()) {
					continue;
				}

				if (propertyType.isEntity()) {
					EntityType<?> propertyEntityType = (EntityType<?>) propertyType;

					configurableEntityGraphNode.add(entityPropertyGraphNode(propertyEntityType, property, entityTypeStack));

				} else if (propertyType.isCollection()) {

					CollectionType collectionType = (CollectionType) propertyType;

					switch (collectionType.getCollectionKind()) {
						case list:
						case set:
							LinearCollectionType linearCollectionType = (LinearCollectionType) collectionType;
							GenericModelType elementType = linearCollectionType.getCollectionElementType();
							if (elementType.isEntity()) {
								EntityType<?> elementEntityType = (EntityType<?>) elementType;
								configurableEntityGraphNode.add(entityCollectionPropertyGraphNode(elementEntityType, property, entityTypeStack));
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

	private static EntityPropertyGraphNode entityPropertyGraphNode(EntityType<?> entityType, Property property, Set<EntityType<?>> entityTypeStack) {

		ConfigurableEntityPropertyGraphNode configurableEntityGraphNode = new ConfigurableEntityPropertyGraphNode(property, entityType);

		fillProperties(entityType, configurableEntityGraphNode, entityTypeStack);

		return configurableEntityGraphNode;
	}

	private static EntityCollectionPropertyGraphNode entityCollectionPropertyGraphNode(EntityType<?> entityType, Property property,
			Set<EntityType<?>> entityTypeStack) {

		ConfigurableEntityCollectionPropertyGraphNode configurableEntityGraphNode = new ConfigurableEntityCollectionPropertyGraphNode(property,
				entityType);

		fillProperties(entityType, configurableEntityGraphNode, entityTypeStack);

		return configurableEntityGraphNode;
	}

}

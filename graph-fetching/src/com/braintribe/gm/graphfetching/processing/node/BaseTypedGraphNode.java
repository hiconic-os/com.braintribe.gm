package com.braintribe.gm.graphfetching.processing.node;

import java.util.IdentityHashMap;
import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityRelatedPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.MapPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.PolymorphicEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.PropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.TypedGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;

public abstract class BaseTypedGraphNode implements TypedGraphNode {
	private static class NodeVisiting {
		int identity;
		boolean first = false;
	}
	private static class NodeIdm {
		int seq;
		Map<EntityGraphNode, Integer> identities = new IdentityHashMap<>();
		
		public NodeVisiting visit(EntityGraphNode node) {
			NodeVisiting visiting = new NodeVisiting();
			
			visiting.identity = identities.computeIfAbsent(node, n -> {
				visiting.first = true;
				return ++seq;
			});
			
			return visiting;
		}
	}
	
	@Override
	public String stringify() {
		StringBuilder sb = new StringBuilder();
		
		stringify(this, sb, 0, new NodeIdm());
		
		return sb.toString();
	}
	
	
	private void stringify(PropertyGraphNode propertyNode, AbstractEntityGraphNode node, StringBuilder sb, int depth, NodeIdm idm) {
		if (node instanceof EntityGraphNode) {
			stringify(propertyNode, (EntityGraphNode)node, sb, depth, idm);
		}
		else if (node instanceof PolymorphicEntityGraphNode) {
			stringify(propertyNode, (PolymorphicEntityGraphNode)node, sb, depth, idm);
		}
	}
	
	private void stringify(String field, GenericModelType fieldType, AbstractEntityGraphNode node, StringBuilder sb, int depth, NodeIdm idm) {
		if (node instanceof EntityGraphNode) {
			stringify(field, fieldType, (EntityGraphNode)node, sb, depth, idm);
		}
		else if (node instanceof PolymorphicEntityGraphNode) {
			stringify(field, fieldType, (PolymorphicEntityGraphNode)node, sb, depth, idm);
		}
	}
	
	private void stringify(String field, GenericModelType fieldType, PolymorphicEntityGraphNode node, StringBuilder sb, int depth, NodeIdm idm) {
		for (EntityGraphNode entityNode: node.entityNodes()) {
			stringify(field, fieldType, entityNode, sb, depth, idm);
		}
	}
	
	private void stringify(PropertyGraphNode propertyNode, PolymorphicEntityGraphNode node, StringBuilder sb, int depth, NodeIdm idm) {
		for (EntityGraphNode entityNode: node.entityNodes()) {
			stringify(propertyNode, entityNode, sb, depth, idm);
		}
	}
	
	private void stringify(PropertyGraphNode propertyNode, EntityGraphNode entityNode, StringBuilder sb, int depth, NodeIdm idm) {
		if (propertyNode != null) {
			stringify(propertyNode.name(), propertyNode.condensedPropertyType(), entityNode, sb, depth, idm);
		}
		else {
			stringify(null, null, entityNode, sb, depth, idm);
		}
	}
	
	private void stringify(String field, GenericModelType fieldType, EntityGraphNode entityNode, StringBuilder sb, int depth, NodeIdm idm) {
		appendIndent(depth, sb);
		
		EntityType<?> actualEntityType = entityNode.entityType();

		if (field != null) {
			sb.append(field);
			if (fieldType != actualEntityType) {
				sb.append(" as ");
				sb.append(actualEntityType.getShortName());
			}
		}
		else {
			sb.append(actualEntityType.getShortName());
		}
		
		NodeVisiting visit = idm.visit(entityNode);

		sb.append(visit.first? " ": " ^");
		sb.append(visit.identity);
		sb.append('\n');

		if (!visit.first) {
			return;
		}
		
		depth++;
		
		for (EntityPropertyGraphNode subNode: entityNode.entityProperties().values()) {
			stringify(subNode, sb, depth, idm);
		}

		for (EntityCollectionPropertyGraphNode subNode: entityNode.entityCollectionProperties().values()) {
			stringify(subNode, sb, depth, idm);
		}
		
		for (MapPropertyGraphNode subNode: entityNode.mapProperties().values()) {
			stringify(subNode, sb, depth, idm);
		}
		
		for (ScalarCollectionPropertyGraphNode subNode: entityNode.scalarCollectionProperties().values()) {
			stringify(subNode, sb, depth, idm);
		}

	}
	
	private void stringify(PropertyGraphNode propertyNode, StringBuilder sb, int depth, NodeIdm idm) {
		Property property = propertyNode.property();
		
		if (propertyNode instanceof EntityRelatedPropertyGraphNode) {
			EntityRelatedPropertyGraphNode entityRelatedNode = (EntityRelatedPropertyGraphNode)propertyNode;
			stringify(entityRelatedNode, sb, depth, idm);
		}
		else if (propertyNode instanceof MapPropertyGraphNode) {
			MapPropertyGraphNode mapPropertyNode = (MapPropertyGraphNode)propertyNode;
			
			MapType mapType = (MapType) property.getType();
			stringify(property, sb, depth, idm);
			AbstractEntityGraphNode keyEntityNode = mapPropertyNode.keyNode();
			AbstractEntityGraphNode valueEntityNode = mapPropertyNode.valueNode();
			
			if (keyEntityNode != null)
				stringify("KEY", mapType.getKeyType(), keyEntityNode, sb, depth + 1, idm);
			if (valueEntityNode != null)
				stringify("VAL", mapType.getValueType(), valueEntityNode, sb, depth + 1, idm);
		}
		else {
			stringify(property, sb, depth, idm);
		}
	}
	
	private void stringify(EntityRelatedPropertyGraphNode entityRelatedNode, StringBuilder sb, int depth, NodeIdm idm) {
		stringify(entityRelatedNode, entityRelatedNode.entityNode(), sb, depth, idm);
	}
	
	private void stringify(ScalarCollectionPropertyGraphNode scalarCollectionNode, StringBuilder sb, int depth, NodeIdm idm) {
		stringify(scalarCollectionNode.property(), sb, depth, idm);
	}
	
	private void stringify(Property property, StringBuilder sb, int depth, NodeIdm idm) {
		appendIndent(depth, sb);
		sb.append(property.getName());
		sb.append('\n');
	}
	
	private void stringify(TypedGraphNode node, StringBuilder sb, int depth, NodeIdm idm) {
		if (node instanceof PropertyGraphNode) {
			PropertyGraphNode propertyNode = (PropertyGraphNode)node;
			stringify(propertyNode, sb, depth, idm);
		}
		else if (node instanceof PolymorphicEntityGraphNode) {
			PolymorphicEntityGraphNode polymorphicEntityNode = (PolymorphicEntityGraphNode)node;
			stringify(null, polymorphicEntityNode, sb, depth, idm);
		}
		else if (node instanceof EntityGraphNode) {
			EntityGraphNode entityNode = (EntityGraphNode)node;
			stringify(null, entityNode, sb, depth, idm);
		}
	}
	
	private void appendIndent(int depth, StringBuilder sb) {
		for (int i = 0; i < depth; i++)
			sb.append("  ");
	}

}

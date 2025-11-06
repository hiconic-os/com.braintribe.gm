package com.braintribe.gm.graphfetching.processing.node;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.PropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.TypedGraphNode;
import com.braintribe.model.generic.reflection.EntityType;

public abstract class BaseTypedGraphNode implements TypedGraphNode {
	@Override
	public String stringify() {
		StringBuilder sb = new StringBuilder();
		
		stringify(this, sb, 0, new HashSet<>());
		
		return sb.toString();
	}
	
	private void stringify(TypedGraphNode node, StringBuilder sb, int depth, Set<EntityGraphNode> visited) {
		appendIndent(depth, sb);
		
		sb.append(node.name());
		
		if (node instanceof PropertyGraphNode) {
			PropertyGraphNode propertyNode = (PropertyGraphNode)node;
			
			if (propertyNode.condensedPropertyType() != node.condensedType()) {
				EntityType<?> entityType = (EntityType<?>)node.condensedType();
				sb.append(" as ");
				sb.append(entityType.getShortName());
			}
		}
		
		sb.append('\n');
		
		if (node instanceof EntityGraphNode) {
			EntityGraphNode entityNode = (EntityGraphNode)node;
			
			if (!visited.add(entityNode))
				return;
				
			depth++;
			
			for (EntityPropertyGraphNode subNode: entityNode.entityProperties()) {
				stringify(subNode, sb, depth, visited);
			}
	
			for (EntityCollectionPropertyGraphNode subNode: entityNode.entityCollectionProperties()) {
				stringify(subNode, sb, depth, visited);
			}
			
			for (ScalarCollectionPropertyGraphNode subNode: entityNode.scalarCollectionProperties()) {
				stringify(subNode, sb, depth, visited);
			}
		}
	}
	
	private void appendIndent(int depth, StringBuilder sb) {
		for (int i = 0; i < depth; i++)
			sb.append("  ");
	}

}

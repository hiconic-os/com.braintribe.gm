package com.braintribe.gm.graphfetching.processing.node;

import java.util.IdentityHashMap;
import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityRelatedPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.PropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.TypedGraphNode;
import com.braintribe.model.generic.reflection.EntityType;

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
	
	private void stringify(TypedGraphNode node, StringBuilder sb, int depth, NodeIdm idm) {
		appendIndent(depth, sb);
		
		sb.append(node.name());
		
		EntityGraphNode entityNode = null;
		
		if (node instanceof PropertyGraphNode) {
			PropertyGraphNode propertyNode = (PropertyGraphNode)node;
			
			if (propertyNode.condensedPropertyType() != node.condensedType()) {
				EntityType<?> entityType = (EntityType<?>)node.condensedType();
				sb.append(" as ");
				sb.append(entityType.getShortName());
			}
			
			if (node instanceof EntityRelatedPropertyGraphNode) {
				entityNode = ((EntityRelatedPropertyGraphNode)node).entityNode();
			}
		}
		else if (node instanceof EntityGraphNode) {
			entityNode = (EntityGraphNode)node;
		}

		if (entityNode == null) {
			sb.append('\n');
			return;
		}
		
		NodeVisiting visit = idm.visit(entityNode);

		sb.append(visit.first? " ": " ^");
		sb.append(visit.identity);
		sb.append('\n');

		if (!visit.first) {
			return;
		}
		
		depth++;
		
		for (EntityPropertyGraphNode subNode: entityNode.entityProperties()) {
			stringify(subNode, sb, depth, idm);
		}

		for (EntityCollectionPropertyGraphNode subNode: entityNode.entityCollectionProperties()) {
			stringify(subNode, sb, depth, idm);
		}
		
		for (ScalarCollectionPropertyGraphNode subNode: entityNode.scalarCollectionProperties()) {
			stringify(subNode, sb, depth, idm);
		}
	}
	
	private void appendIndent(int depth, StringBuilder sb) {
		for (int i = 0; i < depth; i++)
			sb.append("  ");
	}

}

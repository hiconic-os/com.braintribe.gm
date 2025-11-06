package com.braintribe.gm.graphfetching.processing.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.model.generic.reflection.EntityType;

public class ConfigurableInferableGraphNode implements InferableGraphNode {
	private List<InferableGraphNode> subNodes = new ArrayList<>();
	private String name;
	private EntityType<?> entityType;
	
	public ConfigurableInferableGraphNode(String name, InferableGraphNode... subNodes) {
		this.name = name;
		this.subNodes = Arrays.asList(subNodes);
	}
	
	public ConfigurableInferableGraphNode(EntityType<?> entityType, String name, InferableGraphNode... subNodes) {
		this(name, subNodes);
		this.entityType = entityType;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public List<InferableGraphNode> subNodes() {
		return subNodes;
	}
	
	@Override
	public EntityType<?> entityType() {
		return entityType;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String stringify() {
		StringBuilder sb = new StringBuilder();
		
		stringify(this, sb, 0, new HashSet<>());
		
		return sb.toString();
	}
	
	private void stringify(InferableGraphNode node, StringBuilder sb, int depth, Set<InferableGraphNode> visited) {
		appendIndent(depth, sb);
		
		sb.append(node.name());
		
		if (node.entityType() != null) {
			sb.append(" as ");
			sb.append(node.entityType().getShortName());
		}
		
		sb.append('\n');
		
		if (!visited.add(node))
			return;
		
		depth++;
		
		for (InferableGraphNode subNode: node.subNodes()) {
			stringify(subNode, sb, depth, visited);
		}
	}
	
	private void appendIndent(int depth, StringBuilder sb) {
		for (int i = 0; i < depth; i++)
			sb.append("  ");
	}
}

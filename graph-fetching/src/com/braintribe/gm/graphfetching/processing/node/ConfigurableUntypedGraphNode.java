package com.braintribe.gm.graphfetching.processing.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.braintribe.gm.graphfetching.api.node.UntypedGraphNode;

public class ConfigurableUntypedGraphNode implements UntypedGraphNode {
	private List<UntypedGraphNode> subNodes = new ArrayList<>();
	private String name;
	
	public ConfigurableUntypedGraphNode(String name, UntypedGraphNode... subNodes) {
		this.name = name;
		this.subNodes = Arrays.asList(subNodes);
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public List<UntypedGraphNode> subNodes() {
		return subNodes;
	}
	
	@Override
	public String toString() {
		return name;
	}
}

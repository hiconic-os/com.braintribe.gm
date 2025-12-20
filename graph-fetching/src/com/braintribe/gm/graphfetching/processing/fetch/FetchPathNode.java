package com.braintribe.gm.graphfetching.processing.fetch;

import com.braintribe.gm.graphfetching.api.node.TypedGraphNode;

public class FetchPathNode {
	private TypedGraphNode graphNode;
	private FetchPathNode predecessor;
	private String strRep;
	
	public FetchPathNode(TypedGraphNode graphNode) {
		this.graphNode = graphNode;
	}
	
	public FetchPathNode(FetchPathNode predecessor, TypedGraphNode graphNode ) {
		this.graphNode = graphNode;
		this.predecessor = predecessor;
	}
	
	@Override
	public String toString() {
		if (strRep == null) {
			strRep = stringify();
		}
		return strRep;
	}

	private String stringify() {
		StringBuilder builder = new StringBuilder();
		
		stringify(this, builder, 0);
		
		return builder.toString();
	}
	
	private void stringify(FetchPathNode node, StringBuilder builder, int depth) {
		if (depth > 20) {
			builder.append("...");
			return;
		}

		FetchPathNode pre = node.predecessor;
		if (pre != null)
			stringify(pre, builder, depth + 1);
		
		builder.append('/');
		builder.append(node.graphNode.name());
	}
}

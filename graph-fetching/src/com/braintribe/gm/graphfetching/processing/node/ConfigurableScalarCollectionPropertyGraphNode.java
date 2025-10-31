package com.braintribe.gm.graphfetching.processing.node;

import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableScalarCollectionPropertyGraphNode extends ConfigurableCollectionGraphNode implements ScalarCollectionPropertyGraphNode {
	public ConfigurableScalarCollectionPropertyGraphNode(Property property) {
		super(property);
	}
}

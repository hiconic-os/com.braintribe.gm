package com.braintribe.gm.graphfetching.api.node;

public interface PolymorphicEntityGraphNode extends AbstractEntityGraphNode {
	@Override
	default PolymorphicEntityGraphNode isPolymorphic() {
		return this;
	}
}

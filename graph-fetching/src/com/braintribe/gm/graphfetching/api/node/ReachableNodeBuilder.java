package com.braintribe.gm.graphfetching.api.node;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

public interface ReachableNodeBuilder {

	ReachableNodeBuilder covariance(Function<EntityType<?>, Collection<? extends EntityType<?>>> covariance);
	ReachableNodeBuilder covariance(ModelOracle modelOracle);
	ReachableNodeBuilder typeExclusion(Predicate<EntityType<?>> typeExclusion);

	EntityGraphNode build();
}

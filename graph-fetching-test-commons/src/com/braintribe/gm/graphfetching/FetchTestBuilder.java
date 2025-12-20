package com.braintribe.gm.graphfetching;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import com.braintribe.gm.graphfetching.api.FetchBuilder;
import com.braintribe.model.generic.GenericEntity;


public interface FetchTestBuilder {
	FetchTestBuilder add(FetchBuilder builder, boolean detached);
	
	<E extends GenericEntity, C extends Collection<E>> void test(C expected, Supplier<C> resolve);
	
	default <E extends GenericEntity> void test(E expected, Supplier<E> resolve) {
		test(Collections.singletonList(expected), () -> Collections.singletonList(resolve.get()));
	}
}

package com.braintribe.gm.graphfetching;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import com.braintribe.gm.graphfetching.api.FetchBuilder;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;


public interface FetchTestBuilder {
	FetchTestBuilder add(FetchBuilder builder, boolean detached);
	FetchTestBuilder add(PersistenceGmSession session, TraversingCriterion tc, boolean detached);
	
	default FetchTestBuilder addAllTc(PersistenceGmSession session, boolean detached) {
		return add(session, TC.create().negation().joker().done(), detached);
	}
	
	<E extends GenericEntity, C extends Collection<E>> void test(C expected, Supplier<C> resolve);
	
	default <E extends GenericEntity> void test(E expected, Supplier<E> resolve) {
		test(Collections.singletonList(expected), () -> Collections.singletonList(resolve.get()));
	}
}

// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.query.support;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.braintribe.logging.Logger;
import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.eval.api.function.QueryFunctionAspect;
import com.braintribe.model.processing.query.eval.api.function.QueryFunctionExpert;
import com.braintribe.model.processing.query.eval.api.function.aspect.LocaleQueryAspect;
import com.braintribe.model.processing.query.eval.api.repo.Repository;
import com.braintribe.model.processing.query.eval.context.BasicQueryEvaluationContext;
import com.braintribe.model.processing.query.planner.QueryPlanner;
import com.braintribe.model.processing.query.tools.QueryPlanPrinter;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.model.query.functions.QueryFunction;
import com.braintribe.model.queryplan.QueryPlan;

/**
 * Utility class for evaluating queries on top of a given {@link Repository}. Basically it handles {@link EntityQuery}
 * and {@link PropertyQuery} by converting them to a {@link SelectQuery} and forwarding to given
 * {@link IncrementalAccess} ( {@link #selectQueryEvaluator}).
 * 
 * The {@link SelectQuery} itself is evaluated using the Smood's {@link QueryPlanner} and {@link QueryEvaluator}, but
 * using the given {@link Repository} (coming from configured {@link #repositoryProvider}).
 * 
 * @author peter.gazdik
 */
public class QueryEvaluator {

	private static final Logger log = Logger.getLogger(QueryEvaluator.class);

	private final IncrementalAccess selectQueryEvaluator;
	private final Supplier<Repository> repositoryProvider;
	private final Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> functionExperts;

	private final Map<Class<? extends QueryFunctionAspect<?>>, Supplier<?>> functionAspectProviders = newMap();
	private final ReentrantLock functionAspectProvidersLock = new ReentrantLock();

	public QueryEvaluator(IncrementalAccess selectQueryEvaluator, Supplier<Repository> repositoryProvider,
			Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> functionExperts) {

		this.selectQueryEvaluator = selectQueryEvaluator;
		this.repositoryProvider = repositoryProvider;
		this.functionExperts = functionExperts;
	}

	public void setLocaleProvider(Supplier<String> localeProvider) {
		addQueryFunctionAspectProvider(LocaleQueryAspect.class, localeProvider);
	}

	public <T, A extends QueryFunctionAspect<? super T>> void addQueryFunctionAspectProvider(Class<A> aspect, Supplier<T> provider) {
		functionAspectProvidersLock.lock();
		try {
			functionAspectProviders.put(aspect, provider);
		} finally {
			functionAspectProvidersLock.unlock();
		}
	}

	public EntityQueryResult queryEntities(EntityQuery entityQuery) throws ModelAccessException {
		return QueryAdaptingTools.queryEntities(entityQuery, selectQueryEvaluator);
	}

	public PropertyQueryResult queryProperty(PropertyQuery propertyQuery) throws ModelAccessException {
		return QueryAdaptingTools.queryProperties(propertyQuery, selectQueryEvaluator);
	}

	public SelectQueryResult query(SelectQuery query) {
		logSelectQuery(query);

		Repository repository = getRepository();
		QueryPlan queryPlan = new QueryPlanner(repository).buildQueryPlan(query);

		logQueryPlan(queryPlan);

		QueryEvaluationContext context = new BasicQueryEvaluationContext(repository, queryPlan, functionExperts, functionAspectProviders);
		EvalTupleSet tuples = context.resolveTupleSet(queryPlan.getTupleSet());

		return QueryResultBuilder.buildQueryResult(tuples, context.resultComponentsCount());
	}

	private Repository getRepository() {
		try {
			return repositoryProvider.get();

		} catch (RuntimeException e) {
			throw new RuntimeQueryEvaluationException("Error while providing repository for evaluating a query!", e);
		}
	}

	private static void logSelectQuery(SelectQuery query) {
		if (log.isTraceEnabled())
			log.trace("Planning select query: " + QueryPlanPrinter.printSafe(query));
	}

	private static void logQueryPlan(QueryPlan queryPlan) {
		if (log.isTraceEnabled())
			log.trace(QueryPlanPrinter.printSafe(queryPlan));
	}

}

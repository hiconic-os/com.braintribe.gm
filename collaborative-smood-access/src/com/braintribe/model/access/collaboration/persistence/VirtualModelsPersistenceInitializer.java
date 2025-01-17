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
package com.braintribe.model.access.collaboration.persistence;

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.ModelMetaData;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.processing.query.tools.PreparedQueries;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.collaboration.SimplePersistenceInitializer;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.query.SelectQuery;

/**
 * <p>
 * Assembles virtual models based on the given model name and dependencies.
 * 
 */
public class VirtualModelsPersistenceInitializer extends SimplePersistenceInitializer {

	private static final Logger log = Logger.getLogger(VirtualModelsPersistenceInitializer.class);

	private Map<String, Set<String>> virtualModels;
	private Supplier<Set<String>> modelMetaDataGidsSupplier;

	@Configurable
	public void setVirtualModels(Map<String, Set<String>> virtualModels) {
		this.virtualModels = virtualModels;
	}

	@Configurable
	public void setModelMetaDataGidsSupplier(Supplier<Set<String>> modelMetaDataGidsSupplier) {
		this.modelMetaDataGidsSupplier = modelMetaDataGidsSupplier;
	}

	@Override
	public void initializeModels(PersistenceInitializationContext context) {
		if (virtualModels == null)
			return;

		ManagedGmSession session = context.getSession();

		List<ModelMetaData> metaDataToAttach = getMetaDataToAttach(session);

		for (Entry<String, Set<java.lang.String>> entry : virtualModels.entrySet())
			createVirtualModel(session, entry.getKey(), entry.getValue(), metaDataToAttach);
	}

	private List<ModelMetaData> getMetaDataToAttach(ManagedGmSession session) {
		if (modelMetaDataGidsSupplier == null)
			return Collections.emptyList();

		Set<String> gids = modelMetaDataGidsSupplier.get();
		SelectQuery queryForMds = PreparedQueries.entitiesByGlobalIds(ModelMetaData.T, gids);

		return session.query().select(queryForMds).list();
	}

	private void createVirtualModel(ManagedGmSession session, String modelName, Set<String> dependencies, List<ModelMetaData> md) {
		if (isEmpty(dependencies))
			throw new IllegalArgumentException(modelName + " has no dependency configured. A virtual model must have dependencies.");

		List<GmMetaModel> packagedDependencies = queryDependencies(session, modelName, dependencies);

		GmMetaModel virtualModel = session.create(GmMetaModel.T, Model.modelGlobalId(modelName));
		virtualModel.setName(modelName);
		virtualModel.getDependencies().addAll(packagedDependencies);
		virtualModel.getMetaData().addAll(md);

		log.debug(() -> "Assembled virtual model [ " + modelName + " ] with dependencies " + dependencies);
	}

	private List<GmMetaModel> queryDependencies(ManagedGmSession session, String modelName, Set<String> dependencies) {
		SelectQuery query = modelByNames(dependencies);

		List<GmMetaModel> result = session.query().select(query).list();

		if (result.size() != dependencies.size()) {
			List<String> found = result.stream().map(GmMetaModel::getName).collect(Collectors.toList());
			dependencies.removeAll(found);
			throw new IllegalStateException(modelName + " dependency(ies) not found: " + dependencies);
		}

		return result;
	}

	static SelectQuery modelByNames(Set<String> modelNames) {
		return new SelectQueryBuilder().from(GmMetaModel.T, "m").where().property("m", "name").in(modelNames).done();
	}

}

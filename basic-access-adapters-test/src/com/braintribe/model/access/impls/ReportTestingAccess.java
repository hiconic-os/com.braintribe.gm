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
package com.braintribe.model.access.impls;

import java.util.Collection;

import com.braintribe.model.access.BasicAccessAdapter;
import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.smood.Smood;

public class ReportTestingAccess extends BasicAccessAdapter {

	private AdapterManipulationReport report;
	private Smood dataSource;

	public ReportTestingAccess(Smood dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	protected Collection<GenericEntity> loadPopulation() throws ModelAccessException {
		return dataSource.getEntitiesPerType(GMF.getTypeReflection().getEntityType(GenericEntity.class));
	}

	@Override
	protected void save(AdapterManipulationReport context) throws ModelAccessException {
		this.report = context; 
	}
	
	public AdapterManipulationReport getReport() {
		return report;
	}

}

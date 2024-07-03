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
package com.braintribe.model.processing.manipulation.parser.impl;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static java.util.Collections.emptySet;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.manipulation.parser.api.GmmlManipulatorErrorHandler;
import com.braintribe.model.processing.manipulation.parser.api.MutableGmmlManipulatorParserConfiguration;
import com.braintribe.model.processing.manipulation.parser.api.ProblematicEntitiesRegistry;
import com.braintribe.model.processing.manipulation.parser.impl.listener.error.LenientErrorHandler;
import com.braintribe.model.processing.manipulation.parser.impl.listener.error.StrictErrorHandler;

public class BasicGmmlParserConfiguration implements MutableGmmlManipulatorParserConfiguration {

	private String stageName;
	private boolean parseSingleBlock = true;
	private GmmlManipulatorErrorHandler errorHandler;
	private Map<String, Object> variables;
	private Set<GenericEntity> createdEntities;
	private Set<String> homeopathicVariables;
	private boolean bufferEntireInput;
	private ProblematicEntitiesRegistry registry;

	@Override
	public String stageName() {
		return stageName;
	}

	@Override
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	@Override
	public void setParseSingleBlock(boolean parseSingleBlock) {
		this.parseSingleBlock = parseSingleBlock;
	}

	@Override
	public boolean parseSingleBlock() {
		return parseSingleBlock;
	}

	@Override
	@Deprecated
	public void setLenient(boolean lenient) {
		setErrorHandler(lenient ? LenientErrorHandler.INSTANCE : StrictErrorHandler.INSTANCE);
	}

	@Override
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	@Override
	public Map<String, Object> variables() {
		return variables != null ? variables : newMap();
	}

	@Override
	public void setPreviouslyCreatedEntities(Set<GenericEntity> createdEntities) {
		this.createdEntities = createdEntities;
	}

	@Override
	public Set<GenericEntity> previouslyCreatedEntities() {
		return createdEntities != null ? createdEntities : emptySet();
	}

	@Override
	public void setHomeopathicVariables(Set<String> homeopathicVariables) {
		this.homeopathicVariables = homeopathicVariables;
	}

	@Override
	public Set<String> homeopathicVariables() {
		return homeopathicVariables != null ? homeopathicVariables : newSet();
	}

	@Override
	public boolean bufferEntireInput() {
		return bufferEntireInput;
	}

	@Override
	public void setBufferEntireInput(boolean bufferEntireInput) {
		this.bufferEntireInput = bufferEntireInput;
	}

	@Override
	public ProblematicEntitiesRegistry problematicEntitiesRegistry() {
		return registry;
	}

	@Override
	public void setProblematicEntitiesRegistry(ProblematicEntitiesRegistry registry) {
		this.registry = registry;
	}

	@Override
	public GmmlManipulatorErrorHandler errorHandler() {
		return errorHandler;
	}

	@Override
	public void setErrorHandler(GmmlManipulatorErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

}

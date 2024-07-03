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
package com.braintribe.model.processing.meta.cmd;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.experts.SelectorExpert;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

/**
 * Wrapper for all the parameters for configuration of {@link CmdResolverImpl}.
 */
public class ResolutionContextInfo {

	private final ModelOracle modelOracle;
	private Map<EntityType<? extends MetaDataSelector>, SelectorExpert<?>> experts = newMap();
	private Map<Class<? extends SelectorContextAspect<?>>, Object> aspectValues = Collections.emptyMap();
	private Map<Class<? extends SelectorContextAspect<?>>, Supplier<?>> dynamicAspectValueProviders; // nullable
	private Set<MetaData> defaultMetaData = Collections.emptySet();
	private Supplier<?> sessionProvider; // nullable
	private int maxSessionCacheSize;

	public ResolutionContextInfo(ModelOracle modelOracle) {
		this.modelOracle = modelOracle;
	}

	public ModelOracle getModelOracle() {
		return modelOracle;
	}

	/** Sets the {@link SelectorExpert}s */
	public void setExperts(Map<EntityType<? extends MetaDataSelector>, SelectorExpert<?>> experts) {
		this.experts = experts;
	}

	public Map<EntityType<? extends MetaDataSelector>, SelectorExpert<?>> getExperts() {
		return experts;
	}

	/**
	 * Sets the 'static' aspects for given resolver. This gives us an option to provide some aspects which should be
	 * part of every {@link SelectorContext} (i.e. for every single resolution). This may be for instance used for
	 * aspects describing the environment like operating system, JVM version or word size (32/64 bit).
	 */
	public void setStaticAspects(Map<Class<? extends SelectorContextAspect<?>>, Object> aspectValues) {
		this.aspectValues = aspectValues;
	}

	public Map<Class<? extends SelectorContextAspect<?>>, Object> getStaticAspects() {
		return aspectValues;
	}

	/**
	 * Sets providers which are able to provide a given {@link SelectorContextAspect}. The resolver tries to retrieve a
	 * given aspect only iff it does not the value specified in the {@link SelectorContext} directly. This may be
	 * therefore used to provide a backup (overridable default) value of aspect.
	 */
	public void setDynamicAspectValueProviders(Map<Class<? extends SelectorContextAspect<?>>, Supplier<?>> dynamicAspectValueProviders) {
		this.dynamicAspectValueProviders = dynamicAspectValueProviders;
	}

	public Map<Class<? extends SelectorContextAspect<?>>, Supplier<?>> getDynamicAspectValueProviders() {
		return dynamicAspectValueProviders;
	}

	/**
	 * Default {@link MetaData} for given meta data type.
	 */
	public Set<MetaData> getDefaultMetaData() {
		return defaultMetaData;
	}

	public void setDefaultMetaData(Set<MetaData> defaultMetaData) {
		this.defaultMetaData = defaultMetaData;
	}

	/**
	 * Sets a provider, which provides an object that represents a given session. The provided object itself doesn't
	 * have to be the session, it's just important, that the provider returns the same value for all invocations within
	 * the same session. This object is merely used as a key in the session-scoped cache.
	 */
	public void setSessionProvider(Supplier<?> sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public Supplier<?> getSessionProvider() {
		return sessionProvider;
	}

	/**
	 * Sets the maximum number of sessions for which meta data are cached at the same time. If we try to retrieve meta
	 * data for more sessions at once, some of the cached entries must be removed before we put new entries to the
	 * cache.
	 */
	public void setMaxSessionCacheSize(int maxSessionCacheSize) {
		this.maxSessionCacheSize = maxSessionCacheSize;
	}

	public int getMaxSessionCacheSize() {
		return maxSessionCacheSize;
	}
}

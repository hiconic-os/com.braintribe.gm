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
package com.braintribe.model.processing.session.api.managed;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.MetaData;

public interface ModelAccessoryFactory extends ModelAccessorySupplier {

	/**
	 * Returns a {@link ModelAccessory} for given access id.
	 * <p>
	 * IMPORTANT: It may return an instance even if no such access exists, in which case only the subsequent usage (e.g. calling
	 * {@link ModelAccessory#getCmdResolver()}) would throw an exception.
	 */
	default ModelAccessory getForAccess(@SuppressWarnings("unused") String accessId) {
		throw new UnsupportedOperationException("getForAccess(String) is not supported by " + getClass().getName());
	}

	/**
	 * Returns a {@link ModelAccessory} for given service domain id.
	 * <p>
	 * IMPORTANT: It may return an instance even if no such service domain exists, in which case only the subsequent usage (e.g. calling
	 * {@link ModelAccessory#getCmdResolver()}) would throw an exception.
	 */
	default ModelAccessory getForServiceDomain(@SuppressWarnings("unused") String serviceDomainId) {
		throw new UnsupportedOperationException("getForServiceDomain(String) is not supported by " + getClass().getName());
	}

	/***
	 * Returns a {@link ModelAccessoryFactory} with given model perspective. This perspective is a mechanism to cut use-case irrelevant meta-data from
	 * a {@link GmMetaModel model}.
	 * <p>
	 * Perspective is a collection of meta-data domains, and each domain represents a collection of {@link MetaData}.
	 * <p>
	 * If a perspective is applied (on a model), only these meta data are kept and all others are stripped.
	 * <p>
	 * The platform itself defines both "ESSENTIAL" and "BASIC" domains (which pretty much comes down to "essential-meta-data-model" and
	 * "basic-meta-model"), and also defines a "PERSISTENCE_SESSION" perspective, which consists of these two domains. This is used as a mechanism for
	 * a persistence session, especially on a remote system, to only see important meta-data and not say hibernate mappings.
	 * <p>
	 * This perspective can be extended, or a custom can be defined, via methods of HardwiredExpertsContract.
	 * <p>
	 * NOTE that it is possible to call this method again, with a different perspective (i.e.
	 * <code>factory.forPerspective("foo").forPerspective("bar")</code>), which returns a factory that only applies the second perspective(i.e.
	 * "bar"</code>).
	 * 
	 * @param perspective
	 *            the perspective which is applied to strip irrelevant meta-data. <tt>null</tt> means no perspective is applied
	 */
	default ModelAccessoryFactory forPerspective(String perspective) {
		return this;
	}

}

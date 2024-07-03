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
/**
 *
 */
package com.braintribe.utils.genericmodel;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.processing.IdGenerator;

/**
 * A {@link #setDelegate(IdGenerator) delegate} based {@link IdGenerator} that first checks the id property (if there is
 * none, <code>null</code> is returned; if the id is set, the id is returned) before passing the task to the delegate.
 * <br>
 * The implementation is based on <code>biz.i2z.service.ecm.access.impl.generator.SmartIdGenerator</code>.
 *
 * @author michael.lafite
 */
public class SmartIdGenerator implements IdGenerator {

	protected static Logger logger = Logger.getLogger(SmartIdGenerator.class);

	protected IdGenerator delegate;

	public SmartIdGenerator() {
		this(new GuidGenerator());
	}

	public SmartIdGenerator(final IdGenerator delegate) {
		setDelegate(delegate);
	}

	public IdGenerator getDelegate() {
		return this.delegate;
	}

	public void setDelegate(final IdGenerator delegate) {
		this.delegate = delegate;
	}

	@Override
	public Object generateId(final GenericEntity entity) throws Exception {
		final Object idValue = entity.getId();
		if (idValue != null) {
			logger.debug(() -> "Won't generate id for instance of entity type '" + entity.entityType().getTypeSignature()
					+ "', because the id is already set (to '" + idValue + "'). Returning id ...");
			return idValue;
		}

		return this.delegate.generateId(entity);
	}

}

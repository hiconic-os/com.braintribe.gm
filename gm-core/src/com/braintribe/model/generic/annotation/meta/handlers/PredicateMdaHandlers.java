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
package com.braintribe.model.generic.annotation.meta.handlers;

import com.braintribe.model.generic.annotation.meta.Confidential;
import com.braintribe.model.generic.annotation.meta.Emphasized;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.NonDeletable;
import com.braintribe.model.generic.annotation.meta.Singleton;
import com.braintribe.model.generic.annotation.meta.TimeZoneless;
import com.braintribe.model.generic.annotation.meta.Unique;
import com.braintribe.model.generic.annotation.meta.Unmodifiable;
import com.braintribe.model.generic.annotation.meta.api.MdaHandler;
import com.braintribe.model.generic.annotation.meta.base.BasicMdaHandler;

/**
 * @author peter.gazdik
 */
public interface PredicateMdaHandlers {

	MdaHandler<Confidential, com.braintribe.model.meta.data.prompt.Confidential> CONFIDENTIAL = new BasicMdaHandler<>( //
			Confidential.class, com.braintribe.model.meta.data.prompt.Confidential.class, Confidential::globalId);

	MdaHandler<Deprecated, com.braintribe.model.meta.data.prompt.Deprecated> DEPRECATED = new DeprecatedMdaHandler();

	MdaHandler<Emphasized, com.braintribe.model.meta.data.display.Emphasized> EMPHASIZED = new BasicMdaHandler<>( //
			Emphasized.class, com.braintribe.model.meta.data.display.Emphasized.class, Emphasized::globalId);

	MdaHandler<Mandatory, com.braintribe.model.meta.data.constraint.Mandatory> MANDATORY = new BasicMdaHandler<>( //
			Mandatory.class, com.braintribe.model.meta.data.constraint.Mandatory.class, Mandatory::globalId);

	MdaHandler<NonDeletable, com.braintribe.model.meta.data.constraint.NonDeletable> NON_DELETABLE = new BasicMdaHandler<>( //
			NonDeletable.class, com.braintribe.model.meta.data.constraint.NonDeletable.class, NonDeletable::globalId);

	MdaHandler<Singleton, com.braintribe.model.meta.data.prompt.Singleton> SINGLETON = new BasicMdaHandler<>( //
			Singleton.class, com.braintribe.model.meta.data.prompt.Singleton.class, Singleton::globalId);

	MdaHandler<TimeZoneless, com.braintribe.model.meta.data.prompt.TimeZoneless> TIME_ZONELESS = new BasicMdaHandler<>( //
			TimeZoneless.class, com.braintribe.model.meta.data.prompt.TimeZoneless.class, TimeZoneless::globalId);

	MdaHandler<Unique, com.braintribe.model.meta.data.constraint.Unique> UNIQUE = new BasicMdaHandler<>( //
			Unique.class, com.braintribe.model.meta.data.constraint.Unique.class, Unique::globalId);

	MdaHandler<Unmodifiable, com.braintribe.model.meta.data.constraint.Unmodifiable> UNMODIFIABLE = new BasicMdaHandler<>( //
			Unmodifiable.class, com.braintribe.model.meta.data.constraint.Unmodifiable.class, Unmodifiable::globalId);

}

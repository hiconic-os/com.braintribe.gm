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
package com.braintribe.gm.model.reason;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.braintribe.model.generic.GmCoreApiInteropNamespaces;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/**
 * Fluent API that allows to build a reason with its text, causes and custom values. It allows to return the constructed
 * {@link Reason} directly or in form of a {@link Maybe}.
 * 
 * @author Dirk Scheffler
 */
@JsType(namespace = GmCoreApiInteropNamespaces.reason)
public interface ReasonBuilder<R extends Reason> {

	/** Assigns a text to the property {@link Reason#getText()} */
	ReasonBuilder<R> text(String text);

	/**
	 * Assigns a value to the {@link Reason} that's being built.
	 * <p>
	 * <b>Example:</b>
	 * 
	 * <pre>
	 * Reasons.build(HttpError.T) //
	 * 		.assign(HttpError::setStatusCode, 404) //
	 * 		.toReason();
	 * </pre>
	 */
	<T> ReasonBuilder<R> assign(BiConsumer<R, T> assigner, T value);

	/** Adds a {@link Reason} as cause to the property {@link Reason#getReasons()} */
	ReasonBuilder<R> cause(Reason reason);

	/** Adds a number of {@link Reason reasons} as cause to the property {@link Reason#getReasons()} */
	ReasonBuilder<R> causes(Reason... reasons);

	/** Adds a number of {@link Reason reasons} as cause to the property {@link Reason#getReasons()} */
	@JsIgnore
	ReasonBuilder<R> causes(Collection<Reason> reasons);

	/** Allows a {@link Consumer} to enrich the constructed {@link Reason} with custom properties */
	ReasonBuilder<R> enrich(Consumer<R> enricher);

	/** Returns a {@link Maybe} in failed state with the constructed {@link Reason} as {@link Maybe#whyUnsatisfied()} */
	<T> Maybe<T> toMaybe();

	/**
	 * Returns a {@link Maybe} in lenient failed state with the constructed {@link Reason} as {@link Maybe#whyUnsatisfied()} and the passed value as
	 * {@link Maybe#value()}
	 */
	@JsMethod(name = "toIncompleteMaybe")
	<T> Maybe<T> toMaybe(T value);

	/** Returns the constructed {@link Reason} */
	R toReason();
}
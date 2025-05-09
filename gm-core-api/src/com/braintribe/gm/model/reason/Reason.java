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
import java.util.List;
import java.util.Objects;

import com.braintribe.common.potential.Potential;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.Transient;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Reason is the base type of modeled reasoning. It is mainly used to describe expectable failures in operations of all kinds but could also be used
 * in other use-cases. Reason can point to other reasons via it's {@link #getReasons() reasons} property, which can be considered it's
 * causes/precursors.
 * 
 * <p>
 * Each concrete use-case should define a sub type of Reason where the type signature is understood as a unique identifier that can be used to map
 * localization messages. Intermediate super types can be used to further classify Reasons for abstract detection with instanceof operator. Concrete
 * modeling of a Reason per use-case also allows to define custom properties which for example can be referred by localization messages with property
 * placeholders.
 * 
 * <p>
 * Reason defined in gm-core-api is a forward type from the reason-model as it is used on gm-core-api level due to its central role in modeled
 * reasoning.
 * 
 * @author Dirk Scheffler
 */
@ForwardDeclaration("com.braintribe.gm:reason-model")
@SelectiveInformation("${#type_short}")
public interface Reason extends GenericEntity {

	EntityType<Reason> T = EntityTypes.T(Reason.class);

	String code = "code";
	String text = "text";
	String reasons = "reasons";

	@Description("Textual description of the reason.")
	String getText();
	void setText(String text);

	@Description("Reasons related to this reason.")
	List<Reason> getReasons();
	void setReasons(List<Reason> reasons);

	@Transient
	RuntimeException getCreationStackTrace();
	void setCreationStackTrace(RuntimeException creationStackTrace);

	default void causedBy(Reason cause) {
		Objects.requireNonNull(cause, "cause must not be null");

		getReasons().add(cause);
	}

	/**
	 * @deprecated use {@link #stringify()} instead
	 */
	@Deprecated
	default String asFormattedText() {
		return stringify();
	}

	@Override
	default String stringify() {
		return Reasons.format(this);
	}

	default String stringify(boolean includeExceptions) {
		return Reasons.format(this, includeExceptions);
	}

	@Override
	default String asString() {
		String typeSignature = type().getTypeSignature();
		String text = getText();

		if (text != null)
			return typeSignature + ": " + text;
		else
			return typeSignature;
	}

	/** Creates a new {@link Reason} with given text. */
	static Reason create(String text) {
		Reason reason = Reason.T.create();
		reason.setText(text);
		return reason;
	}

	/** Creates a new {@link Reason} with given text and a one other reason which is added to the result's {@link Reason#getReasons()}. */
	static Reason create(String text, Reason previousReason) {
		Reason reason = create(text);
		reason.getReasons().add(previousReason);
		return reason;
	}

	/** Creates a new {@link Reason} with given text and a one other reasons which are all added to the result's {@link Reason#getReasons()}. */
	static Reason create(String text, Collection<Reason> reasons) {
		Reason reason = create(text);
		reason.getReasons().addAll(reasons);
		return reason;
	}

	/** turns the reason into a Potential */
	default <T> Potential<T, Reason> asPotential() {
		return Potential.empty(this, Reason::stringify);
	}

	/** Returns a {@link Maybe} in failed state with this Reason as {@link Maybe#whyUnsatisfied()} */
	default <T> Maybe<T> asMaybe() {
		return Maybe.empty(this);
	}

	/**
	 * Returns a {@link Maybe} in an incomplete state with this Reason as {@link Maybe#whyUnsatisfied()} and the passed value as {@link Maybe#value()}
	 */
	default <T> Maybe<T> asMaybe(T value) {
		return Maybe.incomplete(value, this);
	}

	/**
	 * Starts a new builder for a reason of given type, which has this reason as it's {@link ReasonBuilder#cause(Reason) cause} (i.e. the new reason
	 * is a consequence of this reason).
	 */
	default <R extends Reason> ReasonBuilder<R> buildConsequence(EntityType<R> reasonType) {
		return Reasons.build(reasonType).cause(this);
	}

	default <R extends Reason> R castIfIs(EntityType<? extends R> rType) {
		if (rType.isInstance(this))
			return (R) this;

		return null;
	}

	default <R extends Reason> R castIfIsOneOf(EntityType<? extends R>... rTypes) {
		for (EntityType<? extends R> rType : rTypes) {
			if (rType.isInstance(this))
				return (R) this;
		}

		return null;
	}

	default Throwable linkedThrowable() {
		return getCreationStackTrace();
	}

	default void markStackTrace() {
		setCreationStackTrace(new RuntimeException("<Marked Reason StackTtrace>"));
	}
	
}

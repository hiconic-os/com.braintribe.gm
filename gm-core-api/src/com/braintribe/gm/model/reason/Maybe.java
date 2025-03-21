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

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.braintribe.common.lcd.function.XFunction;
import com.braintribe.exception.Exceptions;
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.model.generic.reflection.EntityType;

import jsinterop.annotations.JsType;

/**
 * Maybe is a wrapper that holds either a value (which can also be <tt>null</tt>) or a {@link Reason} explaining why the value is missing. We call a
 * Maybe with a value "satisfied" and without one "unsatisfied".
 * <p>
 * Maybe can hold both a value and a Reason why it's missing, to allow lenient processing of partial results. This is a special case of an
 * "unsatisfied" Maybe called "incomplete" (because typically it means the value is incomplete and reason is given for why the rest is missing).
 * <p>
 * The value can be obtained with {@link #value()}, which returns <tt>null</tt> if this Maybe has no value, or {@link #get()}, which throws a
 * {@link ReasonException} in case the Maybe is "unsatisfied" (i.e. also in case it has an incomplete value).
 * <p>
 * Quick reference
 * 
 * <pre>
 * |-------|--------|--------------|------------|
 * | value | reason | satisfaction | value mode | 
 * |-------|--------|--------------|------------|
 * |  YES  |  YES   | unsatisfied  | incomplete |
 * |  YES  |  NO    | satisfied    | complete   |
 * |  NO   |  YES   | unsatisfied  | empty      |
 * |  NO   |  NO    |    N/A       | N/A        |
 * |-------|--------|--------------|------------|
 * </pre>
 * 
 * <ul>
 * <li><tt>value</tt> is checked with {@link #hasValue}
 * <li><tt>reason</tt> is checked with {@link #isUnsatisfied()}
 * </ul>
 * 
 * @param <T>
 *            type of the wrapped value
 *
 * @see #hasValue()
 * @see #isEmpty()
 * @see #isSatisfied()
 * @see #isUnsatisfied()
 * @see #isIncomplete()
 * @see #whyUnsatisfied()
 * 
 * @see #value()
 * @see #get()
 * 
 * @author Dirk Scheffler
 */
@JsType(namespace = GmCoreApiInteropNamespaces.reason)
public class Maybe<T> implements Supplier<T> {

	private T value;
	private Reason whyUnsatisfied;
	private boolean hasValue;

	/** Creates a satisfied Maybe with a (possibly <tt>null</tt>) value. */
	public static <T> Maybe<T> complete(T value) {
		Maybe<T> m = new Maybe<>();
		m.hasValue = true;
		m.value = value;
		return m;
	}

	/** Creates an unsatisfied empty Maybe (see class doc) with a {@link Reason} why it's unsatisfied. */
	public static <T> Maybe<T> empty(Reason why) {
		Maybe<T> m = new Maybe<>();
		m.whyUnsatisfied = Objects.requireNonNull(why, "parameter why may not be null");
		return m;
	}

	/** Creates a Maybe (see class doc) with an incomplete value and a {@link Reason} describing why the value is not complete. */
	public static <T> Maybe<T> incomplete(T value, Reason why) {
		Maybe<T> m = new Maybe<>();
		m.value = value;
		m.whyUnsatisfied = Objects.requireNonNull(why, "'why' may not be null");
		m.hasValue = true;
		return m;
	}

	/**
	 * Returns <tt>true</tt> if this Maybe holds a value (even if the value is <tt>null</tt>), regardless if it's {@link #isSatisfied() satisfied} or
	 * {@link #isUnsatisfied() unsatisfied}.
	 */
	public boolean hasValue() {
		return hasValue;
	}

	/**
	 * Returns <tt>true</tt> if this Maybe is satisfied (i.e the value is complete). In such a case {@link #hasValue()} will always return true as
	 * well.
	 */
	public boolean isSatisfied() {
		return whyUnsatisfied == null;
	}

	/**
	 * Returns if the Maybe is in an unsatisfied state (i.e. there is a reason for at least some part of the value missing).
	 */
	public boolean isUnsatisfied() {
		return whyUnsatisfied != null;
	}

	public boolean isUnsatisfiedBy(EntityType<? extends Reason> reasonType) {
		return whyUnsatisfied != null && reasonType.isInstance(whyUnsatisfied);
	}

	public boolean isUnsatisfiedAny(EntityType<? extends Reason>... reasonTypes) {
		if (whyUnsatisfied == null)
			return false;

		for (EntityType<? extends Reason> reasonType : reasonTypes) {
			if (reasonType.isInstance(whyUnsatisfied))
				return true;
		}

		return false;
	}

	/** Returns <tt>true</tt> if this Maybe {@link #hasValue has no value} (which implies it is also {@link #isUnsatisfied() unsatisfied}). */
	public boolean isEmpty() {
		return !hasValue;
	}

	/** Returns <tt>true</tt> if this Maybe is {@link #isUnsatisfied() unsatisfied} but {@link #hasValue() has a value}. */
	public boolean isIncomplete() {
		return isUnsatisfied() && hasValue();
	}

	/** Returns the value of the Maybe. To see if one was supplied {@link #hasValue()} can be checked. */
	public T value() {
		return value;
	}

	/**
	 * Returns the held value if this Maybe is {@link #isSatisfied() satisfied}, throws a {@link ReasonException} based on {@link #whyUnsatisfied}
	 * otherwise.
	 * <p>
	 * To access the value of an unsatisfied Maybe use {@link #value()} instead.
	 */
	@Override
	public T get() throws ReasonException {
		if (whyUnsatisfied != null)
			throw new ReasonException(whyUnsatisfied);

		return value;
	}

	/** Returns the {@link Reason reason} why the Maybe is unsatisfied, if it really is, or <tt>null</tt> if it is not unsatisfied. */
	public <R extends Reason> R whyUnsatisfied() {
		return (R) whyUnsatisfied;
	}

	/**
	 * Returns a Maybe holding this instance's reason, but no value (so that it is compatible even with a different value type).
	 * <p>
	 * Example:
	 *
	 * <pre>{@code
	 * public Maybe<HugType> computeHugeThing() {
	 *     Maybe<PartType> partMaybe = getPart();
	 *     if (partMaybe.isUnsatisfied())
	 *         return partMaybe.propagateReason();
	 *
	 *     PartType part = partMaybe.get();
	 *     ... // continue computing with a value
	 * }
	 * }</pre>
	 *
	 * @throws IllegalStateException
	 *             if this Maybe is satisfied (has not reason)
	 */
	public <X> Maybe<X> propagateReason() {
		if (whyUnsatisfied == null)
			throw new IllegalStateException("Cannot propagate reason, Maybe is satisfied with value: " + value);

		return hasValue ? whyUnsatisfied.asMaybe() : emptyCast();
	}

	public <X> Maybe<X> emptyCast() {
		if (!isEmpty())
			throw new IllegalStateException("Maybe is not empty.");

		return (Maybe<X>) this;
	}

	public <X> Maybe<X> cast() {
		return (Maybe<X>) this;
	}

	public Maybe<T> ifSatisfied(Consumer<? super T> consumer) {
		if (isSatisfied())
			consumer.accept(value);
		return this;
	}

	public Maybe<T> ifValue(Consumer<? super T> consumer) {
		if (hasValue())
			consumer.accept(value);
		return this;
	}

	public Maybe<T> ifUnsatisfied(Consumer<? super Reason> consumer) {
		if (isUnsatisfied())
			consumer.accept(whyUnsatisfied);
		return this;
	}

	public <R> Maybe<R> map(Function<? super T, ? extends R> mapper) {
		requireNonNull(mapper);
		return flatMap(value -> Maybe.complete(mapper.apply(value)));
	}

	public <R> Maybe<R> flatMap(Function<? super T, ? extends Maybe<? extends R>> mapper) {
		requireNonNull(mapper);

		if (isEmpty())
			return cast();

		if (isUnsatisfied())
			return Maybe.empty(whyUnsatisfied);

		try {
			Maybe<? extends R> mapped = mapper.apply(value);
			if (mapped == null)
				throwMappedToNull();

			return mapped.cast();

		} catch (Exception e) {
			throw uncheckedExceptionWhileValueConversion(e);
		}
	}

	public <R> Maybe<R> flatMapLenient(XFunction<Maybe<? extends T>, ? extends Maybe<? extends R>> mapper) {
		requireNonNull(mapper);

		if (isEmpty())
			return cast();

		try {
			Maybe<? extends R> mapped = mapper.apply(this);
			if (mapped == null)
				throwMappedToNull();

			return mapped.cast();

		} catch (Exception e) {
			throw uncheckedExceptionWhileValueConversion(e);
		}
	}

	private void throwMappedToNull() {
		throw new NullPointerException("Mapper returned a null Maybe for value: " + value);
	}

	private RuntimeException uncheckedExceptionWhileValueConversion(Exception e) {
		return Exceptions.unchecked(e, "Error while converting value [" + value + "]. Actual error: " + e.getMessage());
	}

}
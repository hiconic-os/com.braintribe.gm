// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.gm.model.reason.essential.UnsupportedOperation;
import com.braintribe.model.generic.collection.ListBase;
import com.braintribe.model.generic.collection.MapBase;
import com.braintribe.model.generic.collection.PlainList;
import com.braintribe.model.generic.collection.PlainMap;
import com.braintribe.model.generic.collection.PlainSet;
import com.braintribe.model.generic.collection.SetBase;
import com.braintribe.model.generic.enhance.EnhancedEntity;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorEvaluationContext;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorExpert;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorValidationContext;

public class StandardValueDescriptorEvaluationContext implements ValueDescriptorEvaluationContext {

    private static final ValueDescriptorEvaluationPai evaluationPai = new ValueDescriptorEvaluationPai();

    private final ValueDescriptorExpertRegistry registry;
    private final Map<Class<?>, Object> aspects = new LinkedHashMap<>();
    private final IdentityHashMap<ValueDescriptor, Maybe<?>> cache = new IdentityHashMap<>();
    private final IdentityHashMap<ValueDescriptor, Boolean> evaluating = new IdentityHashMap<>();

    public StandardValueDescriptorEvaluationContext(ValueDescriptorExpertRegistry registry) {
        this.registry = registry;
    }

    public <T> StandardValueDescriptorEvaluationContext withAspect(Class<T> aspectType, T value) {
        aspects.put(aspectType, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAspect(Class<T> aspectType) {
        return (T) aspects.get(aspectType);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> Maybe<T> evaluate(ValueDescriptor descriptor) {
        Maybe<?> cached = cache.get(descriptor);
        if (cached != null)
            return (Maybe<T>) cached;

        if (evaluating.put(descriptor, Boolean.TRUE) != null)
            return (Maybe<T>) InvalidArgument.create("Cyclic value descriptor evaluation: " + descriptor.entityType().getTypeSignature()).asMaybe();

        ValueDescriptorExpert expert = registry.find(descriptor.entityType());
        if (expert == null) {
            evaluating.remove(descriptor);
            return (Maybe<T>) UnsupportedOperation.create("No reasoned expert registered for value descriptor: "
                    + descriptor.entityType().getTypeSignature()).asMaybe();
        }

        Maybe<?> result;
        EnhancedEntity enhancedDescriptor = descriptor instanceof EnhancedEntity ? (EnhancedEntity) descriptor : null;
        try {
            if (enhancedDescriptor != null)
                enhancedDescriptor.pushPai(evaluationPai);
            result = ValueDescriptorEvaluationScope.with(this, () -> expert.evaluate(this, descriptor));
            if (result == null)
                result = InternalError.create("Value descriptor expert returned null instead of Maybe: "
                        + descriptor.entityType().getTypeSignature()).asMaybe();
        } catch (UnsatisfiedMaybeTunneling tunneling) {
            result = tunneling.getMaybe();
        } catch (RuntimeException e) {
            result = InternalError.from(e).asMaybe();
        } finally {
            if (enhancedDescriptor != null) {
                Object removedPai = enhancedDescriptor.popPai();
                if (removedPai != evaluationPai)
                    throw new IllegalStateException("Value descriptor expert changed the property access interceptor stack: "
                            + descriptor.entityType().getTypeSignature());
            }
            evaluating.remove(descriptor);
        }

        if (result.isSatisfied() && result.get() != null) {
            GenericModelType valueType = descriptor.valueType();
            if (!valueType.isInstance(result.get()))
                result = InvalidArgument.create("Expert for " + descriptor.entityType().getTypeSignature() + " returned "
                        + result.get().getClass().getName() + " instead of " + valueType.getTypeSignature()).asMaybe();
        }

        if (expert.cacheable(descriptor))
            cache.put(descriptor, result);

        return (Maybe<T>) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Maybe<T> evaluateValue(Object value) {
        return (Maybe<T>) project(value);
    }

    public Reason validate(ValueDescriptor descriptor, GenericModelType expectedType) {
        ValueDescriptorExpert<ValueDescriptor, Object> expert = registry.find(descriptor.entityType());
        if (expert == null)
            return UnsupportedOperation.create("No reasoned expert registered for value descriptor: "
                    + descriptor.entityType().getTypeSignature());

        return expert.validate(new ValidationContext(expectedType), descriptor);
    }

    public Reason complete(ValueDescriptor descriptor, GenericModelType expectedType) {
        ValueDescriptorExpert<ValueDescriptor, Object> expert = registry.find(descriptor.entityType());
        if (expert == null)
            return UnsupportedOperation.create("No reasoned expert registered for value descriptor: "
                    + descriptor.entityType().getTypeSignature());

        return expert.complete(new ValidationContext(expectedType), descriptor);
    }

    private Maybe<?> project(Object value) {
        if (VdHolder.isVdHolder(value))
            return evaluate(((VdHolder) value).vd);
        if (value instanceof ValueDescriptor)
            return evaluate((ValueDescriptor) value);
        if (value instanceof Map<?, ?>)
            return projectMap((Map<?, ?>) value);
        if (value instanceof Collection<?>)
            return projectCollection((Collection<?>) value);
        return Maybe.complete(value);
    }

    private Maybe<?> projectCollection(Collection<?> source) {
        Collection<Object> result = newCollectionLike(source);
        for (Object element : source) {
            Maybe<?> projected = project(element);
            if (projected.isUnsatisfied())
                return projected;
            result.add(projected.get());
        }
        return Maybe.complete(result);
    }

    private Maybe<?> projectMap(Map<?, ?> source) {
        Map<Object, Object> result = newMapLike(source);
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            Maybe<?> projectedKey = project(entry.getKey());
            if (projectedKey.isUnsatisfied())
                return projectedKey;
            Maybe<?> projectedValue = project(entry.getValue());
            if (projectedValue.isUnsatisfied())
                return projectedValue;
            result.put(projectedKey.get(), projectedValue.get());
        }
        return Maybe.complete(result);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Collection<Object> newCollectionLike(Collection<?> source) {
        if (source instanceof ListBase<?>)
            return new PlainList(((ListBase) source).type());
        if (source instanceof SetBase<?>)
            return new PlainSet(((SetBase) source).type());
        if (source instanceof java.util.Set<?>)
            return new LinkedHashSet<>();
        return new ArrayList<>(source.size());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Map<Object, Object> newMapLike(Map<?, ?> source) {
        if (source instanceof MapBase<?, ?>)
            return new PlainMap(((MapBase) source).type());
        return new LinkedHashMap<>();
    }

    private class ValidationContext implements ValueDescriptorValidationContext {
        private final GenericModelType expectedType;

        private ValidationContext(GenericModelType expectedType) {
            this.expectedType = expectedType == null ? BaseType.INSTANCE : expectedType;
        }

        @Override
        public Reason validate(ValueDescriptor descriptor) {
            return StandardValueDescriptorEvaluationContext.this.validate(descriptor, BaseType.INSTANCE);
        }

        @Override
        public GenericModelType expectedType() {
            return expectedType;
        }
    }
}

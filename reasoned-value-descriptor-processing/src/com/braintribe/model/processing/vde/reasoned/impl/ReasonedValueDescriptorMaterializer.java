// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.impl;

import java.util.IdentityHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.clone.AbstractDirectCloning;
import com.braintribe.model.processing.clone.CloneTarget;
import com.braintribe.model.processing.vde.reasoned.api.ResidualValuePolicy;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorEvaluationContext;

/**
 * Produces an independent assembly by evaluating every encountered VD before applying the normal GM type cloning
 * rules. Evaluator results are treated as borrowed and therefore pass through the materializer again.
 */
public class ReasonedValueDescriptorMaterializer extends AbstractDirectCloning {

    private final ValueDescriptorEvaluationContext evaluationContext;
    private final ResidualValuePolicy residualPolicy;
    private final Map<GenericEntity, GenericEntity> materializedClones = new IdentityHashMap<>();
    private final Map<GenericEntity, GenericEntity> descriptorClones = new IdentityHashMap<>();
    private int preservingDescriptorDepth;

    public ReasonedValueDescriptorMaterializer(ValueDescriptorEvaluationContext evaluationContext) {
        this(evaluationContext, ResidualValuePolicy.rejectAll());
    }

    public ReasonedValueDescriptorMaterializer(ValueDescriptorEvaluationContext evaluationContext, ResidualValuePolicy residualPolicy) {
        this.evaluationContext = evaluationContext;
        this.residualPolicy = residualPolicy;
    }

    public <T> Maybe<T> materialize(T value) {
        try {
            return Maybe.complete(cloneValue(value));
        } catch (UnsatisfiedMaybeTunneling tunneling) {
            return tunneling.getMaybe();
        } catch (RuntimeException e) {
            return InternalError.from(e).asMaybe();
        }
    }

    public <T> Maybe<T> materialize(T value, GenericModelType type) {
        try {
            return Maybe.complete(cloneValue(value, type));
        } catch (UnsatisfiedMaybeTunneling tunneling) {
            return tunneling.getMaybe();
        } catch (RuntimeException e) {
            return InternalError.from(e).asMaybe();
        }
    }

    @Override
    protected GenericModelType actualTypeForCloning(GenericModelType declaredType, Object value) {
        return isDescriptorTransport(value) ? declaredType : super.actualTypeForCloning(declaredType, value);
    }

    @Override
    protected Object doCloneValue(GenericModelType type, Object value) {
        if (!isDescriptorTransport(value))
            return super.doCloneValue(type, value);

        if (preservingDescriptorDepth > 0)
            return cloneDescriptorTransport(value);

        ValueDescriptor descriptor = descriptor(value);
        if (descriptor instanceof AbsenceInformation)
            return value;

        Maybe<?> evaluated = evaluationContext.evaluate(descriptor);
        if (evaluated.isUnsatisfied()) {
            if (residualPolicy.preserve(evaluated.whyUnsatisfied()))
                return cloneDescriptorTransport(value);
            throw new UnsatisfiedMaybeTunneling(evaluated);
        }

        Object result = evaluated.get();
        if (result == descriptor || result == value)
            throw new UnsatisfiedMaybeTunneling(InternalError.create(
                    "A satisfied value descriptor evaluation returned its input descriptor: "
                            + descriptor.entityType().getTypeSignature()).asMaybe());

        // The result is borrowed. Re-entering the materializer ensures nested VDs and original graph fragments are handled.
        return doCloneValue(type, result);
    }

    private Object cloneDescriptorTransport(Object value) {
        ValueDescriptor descriptor = descriptor(value);
        if (descriptor instanceof AbsenceInformation)
            return value;

        preservingDescriptorDepth++;
        try {
            ValueDescriptor clone = (ValueDescriptor) super.doCloneValue(descriptor.entityType(), descriptor);
            return VdHolder.isVdHolder(value) ? VdHolder.newInstance(clone) : clone;
        } finally {
            preservingDescriptorDepth--;
        }
    }

    private static boolean isDescriptorTransport(Object value) {
        return VdHolder.isVdHolder(value) || value instanceof ValueDescriptor;
    }

    private static ValueDescriptor descriptor(Object value) {
        return VdHolder.isVdHolder(value) ? ((VdHolder) value).vd : (ValueDescriptor) value;
    }

    @Override
    protected CloneTarget acquireCloneTarget(GenericEntity entity) {
        Map<GenericEntity, GenericEntity> clones = preservingDescriptorDepth > 0 ? descriptorClones : materializedClones;
        GenericEntity known = clones.get(entity);
        if (known != null)
            return new Target(known, false);

        GenericEntity clone = entity.entityType().createRaw();
        clones.put(entity, clone);
        return new Target(clone, true);
    }

    private static class Target implements CloneTarget {
        private final GenericEntity entity;
        private final boolean cloneTransitively;

        private Target(GenericEntity entity, boolean cloneTransitively) {
            this.entity = entity;
            this.cloneTransitively = cloneTransitively;
        }

        @Override
        public GenericEntity getEntity() {
            return entity;
        }

        @Override
        public boolean shouldCloneTransitively() {
            return cloneTransitively;
        }
    }
}

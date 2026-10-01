// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.ReasonAggregator;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.clone.AbstractDirectCloning;
import com.braintribe.model.processing.clone.CloneTarget;
import com.braintribe.model.processing.vde.reasoned.api.ResidualValuePolicy;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorEvaluationContext;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorEvaluationPolicy;

/**
 * Produces an independent assembly by normalizing the complete input graph of every encountered VD before invoking its expert. This makes model
 * properties and collection/map elements obey the same recursive rules and permits independent siblings to be checked even when one sibling fails.
 * Evaluator results are treated as borrowed and therefore pass through the materializer again.
 */
public class ReasonedValueDescriptorMaterializer extends AbstractDirectCloning {

    private final ValueDescriptorEvaluationContext evaluationContext;
    private final ResidualValuePolicy residualPolicy;
    private final Map<GenericEntity, GenericEntity> materializedClones = new IdentityHashMap<>();
    private final Map<ValueDescriptor, PreparedDescriptor> preparedDescriptors = new IdentityHashMap<>();
    private final Deque<PreparationFrame> preparationFrames = new ArrayDeque<>();
    private final IdentityHashMap<ValueDescriptor, Boolean> descriptorRootsBeingCloned = new IdentityHashMap<>();
    private int deferredTreeDepth;
    private ReasonAggregator<InternalError> collectedErrors;

    public ReasonedValueDescriptorMaterializer(ValueDescriptorEvaluationContext evaluationContext) {
        this(evaluationContext, ResidualValuePolicy.rejectAll());
    }

    public ReasonedValueDescriptorMaterializer(ValueDescriptorEvaluationContext evaluationContext, ResidualValuePolicy residualPolicy) {
        this.evaluationContext = evaluationContext;
        this.residualPolicy = residualPolicy;
    }

    public <T> Maybe<T> materialize(T value) {
        collectedErrors = Reasons.aggregatorForceWrap(() -> InternalError.create("Value descriptor materialization failed"));
        try {
            T result = cloneValue(value);
            return collectedErrors.hasReason() ? Maybe.incomplete(result, collectedErrors.get()) : Maybe.complete(result);
        } catch (UnsatisfiedMaybeTunneling tunneling) {
            return tunneling.getMaybe();
        } catch (RuntimeException e) {
            return InternalError.from(e).asMaybe();
        }
    }

    public <T> Maybe<T> materialize(T value, GenericModelType type) {
        collectedErrors = Reasons.aggregatorForceWrap(() -> InternalError.create("Value descriptor materialization failed"));
        try {
            T result = cloneValue(value, type);
            return collectedErrors.hasReason() ? Maybe.incomplete(result, collectedErrors.get()) : Maybe.complete(result);
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
    protected Object cloneNestedValue(GenericModelType type, Object value) {
        try {
            return doCloneValue(type, value);
        } catch (UnsatisfiedMaybeTunneling tunneling) {
            collectedErrors.accept(tunneling.getMaybe().whyUnsatisfied());
            markContainingPreparationFailed();
            // The partial result is diagnostic only when hard errors exist. Keeping the original value lets traversal continue safely.
            return value;
        } catch (RuntimeException e) {
            collectedErrors.accept(InternalError.from(e));
            markContainingPreparationFailed();
            return value;
        }
    }

    @Override
    protected Object clonePropertyValue(GenericEntity entity, Property property, GenericModelType type, Object value) {
        Object cloned = cloneNestedValue(type, value);
        return cloned instanceof ValueDescriptor && (!(entity instanceof ValueDescriptor) || !type.isValueAssignable(cloned))
                ? VdHolder.newInstance((ValueDescriptor) cloned)
                : cloned;
    }

    @Override
    protected Object doCloneValue(GenericModelType type, Object value) {
        if (!isDescriptorTransport(value))
            return super.doCloneValue(type, value);

        ValueDescriptor descriptor = descriptor(value);
        if (descriptor instanceof AbsenceInformation)
            return value;

        if (deferredTreeDepth > 0)
            return cloneDeferredDescriptor(descriptor);

        if (descriptorRootsBeingCloned.containsKey(descriptor))
            return super.doCloneValue(descriptor.entityType(), descriptor);

        ValueDescriptorEvaluationPolicy evaluationPolicy = evaluationContext.getAspect(ValueDescriptorEvaluationPolicy.class);
        Reason deferReason = evaluationPolicy == null ? null : evaluationPolicy.deferReason(evaluationContext, descriptor);
        if (deferReason != null) {
            if (!residualPolicy.preserve(deferReason))
                throw new UnsatisfiedMaybeTunneling(deferReason.asMaybe());

            ValueDescriptor deferred = cloneDeferredDescriptor(descriptor);
            residualPolicy.preserved(deferred, deferReason);
            markContainingPreparationResidual();
            return deferred;
        }

        PreparedDescriptor prepared = prepare(descriptor);
        if (prepared.failed()) {
            markContainingPreparationFailed();
            return prepared.descriptor();
        }
        if (prepared.residual()) {
            markContainingPreparationResidual();
            return prepared.descriptor();
        }

        Maybe<?> evaluated = evaluationContext.evaluate(prepared.descriptor());
        if (evaluated.isUnsatisfied()) {
            if (residualPolicy.preserve(evaluated.whyUnsatisfied())) {
                residualPolicy.preserved(prepared.descriptor(), evaluated.whyUnsatisfied());
                markContainingPreparationResidual();
                return prepared.descriptor();
            }
            throw new UnsatisfiedMaybeTunneling(evaluated);
        }

        Object result = evaluated.get();
        if (result == descriptor || result == prepared.descriptor() || result == value)
            throw new UnsatisfiedMaybeTunneling(InternalError.create(
                    "A satisfied value descriptor evaluation returned its input descriptor: "
                            + descriptor.entityType().getTypeSignature()).asMaybe());

        // The result is borrowed. Re-entering the materializer ensures nested VDs and original graph fragments are handled.
        return doCloneValue(type, result);
    }

    /** Clones a deliberately deferred descriptor without evaluating any descriptor contained in its argument graph. */
    private ValueDescriptor cloneDeferredDescriptor(ValueDescriptor descriptor) {
        descriptorRootsBeingCloned.put(descriptor, Boolean.TRUE);
        deferredTreeDepth++;
        try {
            return (ValueDescriptor) super.doCloneValue(descriptor.entityType(), descriptor);
        } finally {
            deferredTreeDepth--;
            descriptorRootsBeingCloned.remove(descriptor);
        }
    }

    private PreparedDescriptor prepare(ValueDescriptor descriptor) {
        PreparedDescriptor known = preparedDescriptors.get(descriptor);
        if (known != null)
            return known;

        PreparationFrame frame = new PreparationFrame();
        preparationFrames.push(frame);
        descriptorRootsBeingCloned.put(descriptor, Boolean.TRUE);
        try {
            ValueDescriptor prepared = (ValueDescriptor) super.doCloneValue(descriptor.entityType(), descriptor);
            PreparedDescriptor result = new PreparedDescriptor(prepared, frame.residual, frame.failed);
            preparedDescriptors.put(descriptor, result);
            return result;
        } finally {
            descriptorRootsBeingCloned.remove(descriptor);
            preparationFrames.pop();
        }
    }

    private void markContainingPreparationResidual() {
        for (PreparationFrame frame : preparationFrames)
            frame.residual = true;
    }

    private void markContainingPreparationFailed() {
        for (PreparationFrame frame : preparationFrames)
            frame.failed = true;
    }

    private static boolean isDescriptorTransport(Object value) {
        return VdHolder.isVdHolder(value) || value instanceof ValueDescriptor;
    }

    private static ValueDescriptor descriptor(Object value) {
        return VdHolder.isVdHolder(value) ? ((VdHolder) value).vd : (ValueDescriptor) value;
    }

    @Override
    protected CloneTarget acquireCloneTarget(GenericEntity entity) {
        GenericEntity known = materializedClones.get(entity);
        if (known != null)
            return new Target(known, false);

        GenericEntity clone = entity.entityType().createRaw();
        materializedClones.put(entity, clone);
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

    private static class PreparationFrame {
        private boolean residual;
        private boolean failed;
    }

    private static class PreparedDescriptor {
        private final ValueDescriptor descriptor;
        private final boolean residual;
        private final boolean failed;

        private PreparedDescriptor(ValueDescriptor descriptor, boolean residual, boolean failed) {
            this.descriptor = descriptor;
            this.residual = residual;
            this.failed = failed;
        }

        private ValueDescriptor descriptor() {
            return descriptor;
        }

        private boolean residual() {
            return residual;
        }

        private boolean failed() {
            return failed;
        }
    }
}

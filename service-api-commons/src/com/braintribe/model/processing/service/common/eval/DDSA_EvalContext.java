package com.braintribe.model.processing.service.common.eval;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.common.attribute.TypeSafeAttributeEntry;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.eval.AbstractEvalContext;
import com.braintribe.model.generic.eval.EvalException;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.service.api.EvaluatorAspect;
import com.braintribe.model.processing.service.api.ParentAttributeContextAspect;
import com.braintribe.model.processing.service.api.ResponseConsumerAspect;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.api.ServiceRequestContextBuilder;
import com.braintribe.model.processing.service.api.ServiceRequestSummaryLogger;
import com.braintribe.model.processing.service.api.aspect.EagerResponseConsumerAspect;
import com.braintribe.model.processing.service.api.aspect.RequestEvaluationIdAspect;
import com.braintribe.model.processing.service.common.eval.AbstractServiceRequestEvaluator.EagerResultHolder;
import com.braintribe.model.processing.service.commons.ServiceRequestContexts;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.processing.async.api.AsyncCallback;
import com.braintribe.utils.collection.impl.AttributeContexts;

/**
 * @author peter.gazdik
 */
/* package */ class DDSA_EvalContext<T> extends AbstractEvalContext<T> {

	private static final Logger log = Logger.getLogger(DDSA_EvalContext.class);

	private final AbstractServiceRequestEvaluator parent;
	private final ServiceRequest request;
	private final EagerResultHolder responseConsumer = new EagerResultHolder();
	private AttributeContext parentContext;
	private Evaluator<ServiceRequest> evaluator;

	public DDSA_EvalContext(AbstractServiceRequestEvaluator parent, ServiceRequest request) {
		this.parent = parent;
		this.request = request;
	}

	@Override
	public T get() throws EvalException {
		try {
			T result = processSync();
			return result;
		} catch (UnsatisfiedMaybeTunneling m) {
			throw parent.reasonExceptionFactory.apply(m.getMaybe().whyUnsatisfied());
		}
	}

	@Override
	public void get(AsyncCallback<? super T> targetCallback) {
		AsyncCallback<? super T> nullSafeCallback = ensureCallback(targetCallback);

		processAsync(AsyncCallback.of(nullSafeCallback::onSuccess, t -> {
			if (t instanceof UnsatisfiedMaybeTunneling) {
				nullSafeCallback.onFailure(parent.reasonExceptionFactory.apply(((UnsatisfiedMaybeTunneling) t).whyUnsatisfied()));
			} else
				nullSafeCallback.onFailure(t);
		}));
	}

	@Override
	public Maybe<T> getReasoned() {
		try {
			return Maybe.complete(processSync());
		} catch (UnsatisfiedMaybeTunneling m) {
			return m.getMaybe();
		}
	}

	@Override
	public void getReasoned(AsyncCallback<? super Maybe<T>> targetCallback) {
		AsyncCallback<? super Maybe<T>> nullSafeCallback = ensureCallback(targetCallback);

		processAsync(AsyncCallback.of((T v) -> nullSafeCallback.onSuccess(Maybe.complete(v)), t -> {
			if (t instanceof UnsatisfiedMaybeTunneling) {
				nullSafeCallback.onSuccess(((UnsatisfiedMaybeTunneling) t).getMaybe());
			} else
				nullSafeCallback.onFailure(t);
		}));
	}

	@Override
	public <A extends TypeSafeAttribute<? super V>, V> void setAttribute(Class<A> attribute, V value) {
		if (attribute == ResponseConsumerAspect.class) {
			responseConsumer.listener = (Consumer<Object>) value;
			value = (V) responseConsumer;
		} else if (attribute == ParentAttributeContextAspect.class) {
			parentContext = (AttributeContext) value;
		} else if (attribute == EvaluatorAspect.class) {
			evaluator = (Evaluator<ServiceRequest>) value;
		}

		super.setAttribute(attribute, value);
	}

	private T processAsync(final AsyncCallback<? super T> targetCallback) {
		ServiceRequestContext invocationContext = prepareContext();

		processAsync(targetCallback, invocationContext);
		return null;
	}

	private T processSync() {

		ServiceRequestContext invocationContext = prepareContext();

		AttributeContexts.push(invocationContext);

		try {
			return processNormalizedWithSummary(invocationContext);
		} finally {
			AttributeContexts.pop();
		}
	}

	private AttributeContext getParentContext() {
		return parentContext != null ? parentContext : AttributeContexts.peek();
	}

	private boolean filterAttribute(TypeSafeAttributeEntry entry) {
		Class<? extends TypeSafeAttribute<?>> attribute = entry.attribute();

		return !contextAttributeVetos.contains(attribute);
	}

	/* package */ ServiceRequestContext prepareContext() {
		Evaluator<ServiceRequest> effectiveEvaluator = evaluator != null ? evaluator : parent.contextEvaluator;
		final ServiceRequestContextBuilder invocationContextBuilder = ServiceRequestContexts.serviceRequestContext(getParentContext(),
				effectiveEvaluator);

		//@formatter:off
		streamAttributes()
			.filter(this::filterAttribute)
			.forEach(e -> invocationContextBuilder.setAttribute(e.attribute(), e.value()));
		//@formatter:on

		invocationContextBuilder.set(EagerResponseConsumerAspect.class, responseConsumer);
		invocationContextBuilder.set(RequestEvaluationIdAspect.class, UUID.randomUUID().toString());

		return invocationContextBuilder.build();
	}

	private void processAsync(final AsyncCallback<? super T> targetCallback, ServiceRequestContext invocationContext) {
		ServiceRequestSummaryLogger summaryLogger = invocationContext.summaryLogger();
		if (summaryLogger.isEnabled()) {
			String summaryStep = request.entityType().getShortName() + " async evaluation";
			summaryLogger.startTimer(summaryStep);

			try {
				submitAsyncProcessing(invocationContext, targetCallback);
			} finally {
				summaryLogger.stopTimer(summaryStep);
			}
		} else {
			submitAsyncProcessing(invocationContext, targetCallback);
		}
	}

	private void submitAsyncProcessing(ServiceRequestContext context, final AsyncCallback<? super T> targetCallback) {
		parent.executorService.submit(() -> {
			AttributeContexts.push(context);

			try {
				T result = processNormalized(context);
				targetCallback.onSuccess(result);
			} catch (Exception e) {
				targetCallback.onFailure(e);
			} finally {
				AttributeContexts.pop();
			}
		});

		log.trace(() -> "Submitted async " + request.entityType().getShortName() + " evaluation via " + parent.executorService);
	}

	private T processNormalized(ServiceRequestContext context) {
		Object result = parent.serviceProcessor.process(context, request);
		responseConsumer.notifyActualResult(result);
		return (T) responseConsumer.get();
	}

	/* package */ T processNormalizedWithSummary(ServiceRequestContext context) {
		ServiceRequestSummaryLogger summaryLogger = context.summaryLogger();
		if (!summaryLogger.isEnabled())
			return processNormalized(context);

		String summaryStep = request.entityType().getShortName() + " evaluation";

		summaryLogger.startTimer(summaryStep);
		try {
			return processNormalized(context);
		} finally {
			summaryLogger.stopTimer(summaryStep);
		}
	}

	private <V> AsyncCallback<V> ensureCallback(AsyncCallback<V> callback) {
		if (callback != null)
			return callback;

		return AsyncCallback.of( //
				v -> log.debug("Received result [" + v + "] for async request without callback: " + request), //
				t -> log.error("Error while exuting async request without callback [" + request + "]", t) //
		);
	}

	private static Collection<Class<? extends TypeSafeAttribute<?>>> contextAttributeVetos = Arrays.asList(EagerResponseConsumerAspect.class,
			ParentAttributeContextAspect.class);

}
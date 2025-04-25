package com.braintribe.gm.config;

import java.util.function.Function;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.vde.clone.async.AsyncCloningImpl;
import com.braintribe.model.processing.vde.evaluator.VDE;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeRegistry;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.aspects.VariableProviderAspect;
import com.braintribe.model.processing.vde.evaluator.api.builder.VdeContextBuilder;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;
import com.braintribe.processing.async.api.AsyncCallback;
import com.braintribe.provider.Holder;

public abstract class ConfigPlaceholders {
	private static class AiEvaluator implements ValueDescriptorEvaluator<AbsenceInformation> {
		@Override
		public VdeResult evaluate(VdeContext context, AbsenceInformation valueDescriptor) throws VdeRuntimeException {
			return new VdeResultImpl(valueDescriptor, false);
		}
	}
	
	public static <E> Maybe<E> resolvePlaceholders(E config, Function<Variable, Object> resolver) {
		VdeRegistry vdeRegistry = VDE.registryBuilder()
				.loadDefaultSetup()
				.withConcreteExpert(AbsenceInformation.class, new AiEvaluator())
				.done();
		
		VdeContextBuilder builder = VDE.evaluate()
				.withRegistry(vdeRegistry)
				.with(VariableProviderAspect.class, resolver);
		
		Holder<E> resultHolder = new Holder<>();
		Holder<Throwable> errorHolder = new Holder<>();
		
		new AsyncCloningImpl((vd,c) -> c.onSuccess(builder.forValue(vd)), Runnable::run, e -> false)
		.cloneValue(config, AsyncCallback.of(
				resultHolder, 
				errorHolder
				));
		
		config = resultHolder.get();
		Throwable throwable = errorHolder.get();
		
		if (throwable != null) {
			return Maybe.incomplete(config, InternalError.from(throwable));
		}
		
		return Maybe.complete(config);
	}

}

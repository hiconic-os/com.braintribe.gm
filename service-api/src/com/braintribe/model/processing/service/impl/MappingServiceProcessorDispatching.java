package com.braintribe.model.processing.service.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.processing.service.api.MappingServiceProcessor;
import com.braintribe.model.processing.service.api.Service;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class MappingServiceProcessorDispatching<P extends ServiceRequest, R> implements DispatchConfigurator<P, R> {
	
	private MappingServiceProcessor<P, R> annotatedServiceProcessor;

	static <P extends ServiceRequest, R> MappingServiceProcessorDispatching<P, R> create(MappingServiceProcessor<P, R> persistenceProcessor) {
		return new MappingServiceProcessorDispatching<>(persistenceProcessor);
	}
	
	public MappingServiceProcessorDispatching(MappingServiceProcessor<P, R> persistenceProcessor) {
		this.annotatedServiceProcessor = persistenceProcessor;
	}

	@Override
	public void configureDispatching(DispatchConfiguration<P, R> dispatching) {
		Lookup lookup = MethodHandles.lookup();
		for (Method method: annotatedServiceProcessor.getClass().getMethods()) {
			Service serviceAnnotation = method.getAnnotation(Service.class);
			
			if (serviceAnnotation == null)
				continue;
			
			Class<?>[] parameterTypes = method.getParameterTypes();
			
			if (parameterTypes.length != 1)
				throw new UnsupportedOperationException("method " + method + " is annotated with @Service but does not comply to required signature (ServiceRequestContext, ServiceRequest");
			
			Class<?> serviceRequestContextParameterType = parameterTypes[0];
			Class<?> requestParameterType = parameterTypes[1];
			
			if (serviceRequestContextParameterType != ServiceRequestContext.class)
				throw new UnsupportedOperationException("method " + method + " is annotated with @Service but does not comply to required signature (ServiceRequestContext, ServiceRequest");
			
			
			if (!ServiceRequest.class.isAssignableFrom(requestParameterType))
				throw new UnsupportedOperationException("method " + method + " is annotated with Transaction but does not comply to required signature (ServiceRequestContext, ServiceRequest");
			
			try {
				MethodHandle methodHandle = lookup.unreflect(method);
				MethodHandle boundHandle = methodHandle.bindTo(annotatedServiceProcessor);
				
		        EntityType<P> requestType = EntityTypes.T((Class<P>)requestParameterType);
		    
		        if (method.getReturnType() == Maybe.class)
		        	dispatching.register(requestType, new MethodHandleServiceProcessor<>(boundHandle));
		        else
		        	dispatching.registerReasoned(requestType, new ReasonedMethodHandleServiceProcessor<>(boundHandle));
			}
			catch (Error e) {
				throw e;
			}
			catch (Throwable e) {
				throw new IllegalStateException("Unexpected Exception when converting Transaction annotated method to PersistenceServiceProcessor", e);
			}
			
		}
	}
}

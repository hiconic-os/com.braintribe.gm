package com.braintribe.model.processing.service.impl.anno;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.service.api.ReasonedServiceProcessor;
import com.braintribe.model.processing.service.api.Service;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.impl.ServiceProcessors;
import com.braintribe.model.processing.service.impl.anno.model.AnnoTestRequest;
import com.braintribe.model.processing.service.impl.anno.model.EvaluateWithContext;
import com.braintribe.model.processing.service.impl.anno.model.EvaluateWithoutContext;
import com.braintribe.model.processing.service.impl.anno.processors.AnnotatedTestProcessor;
import com.braintribe.model.processing.service.impl.anno.processors.AnnotatedTestReasonedProcessor;

/**
 * Tests for {@link ServiceProcessors} declared using {@link Service} annotation.
 * 
 * @see AnnotatedTestProcessor
 * 
 * @author peter.gazdik
 */
public class AnnotatedServiceProcessorTest {

	@Test
	public void testRegular() throws Exception {
		AnnotatedTestProcessor annotatedProcessor = new AnnotatedTestProcessor();
		ServiceProcessor<AnnoTestRequest, String> processor = ServiceProcessors.dispatcher(annotatedProcessor);

		String result;
		result = processor.process(null, req(EvaluateWithoutContext.T, "x"));
		assertThat(result).isEqualTo("without-x");

		result = processor.process(null, req(EvaluateWithContext.T, "x"));
		assertThat(result).isEqualTo("with-x");
	}

	@Test
	public void testReasoned() throws Exception {
		AnnotatedTestReasonedProcessor annotatedProcessor = new AnnotatedTestReasonedProcessor();
		ReasonedServiceProcessor<AnnoTestRequest, String> processor = (ReasonedServiceProcessor<AnnoTestRequest, String>) ServiceProcessors.dispatcher(annotatedProcessor);

		Maybe<? extends String> result;
		result = processor.processReasoned(null, req(EvaluateWithoutContext.T, "x"));
		assertThat(result).isSatisfied();
		assertThat(result.get()).isEqualTo("without-x");

		result = processor.processReasoned(null, req(EvaluateWithContext.T, "x"));
		assertThat(result).isSatisfied();
		assertThat(result.get()).isEqualTo("with-x");
	}

	private <R extends AnnoTestRequest> R req(EntityType<R> et, String param) {
		R result = et.create();
		result.setParameter(param);

		return result;
	}

}

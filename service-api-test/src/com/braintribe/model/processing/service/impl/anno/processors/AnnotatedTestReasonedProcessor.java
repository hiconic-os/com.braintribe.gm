package com.braintribe.model.processing.service.impl.anno.processors;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.processing.service.api.MappingServiceProcessor;
import com.braintribe.model.processing.service.api.Service;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.impl.anno.model.AnnoTestRequest;
import com.braintribe.model.processing.service.impl.anno.model.EvaluateWithContext;
import com.braintribe.model.processing.service.impl.anno.model.EvaluateWithoutContext;

/**
 * @author peter.gazdik
 */
public class AnnotatedTestReasonedProcessor implements MappingServiceProcessor<AnnoTestRequest, String> {

	@Service
	public Maybe<String> evalWithoutContext(EvaluateWithoutContext request) {
		return Maybe.complete("without-" + request.getParameter());
	}

	@Service
	public Maybe<String> evalWithoutContext(EvaluateWithContext request, @SuppressWarnings("unused") ServiceRequestContext ctx) {
		return Maybe.complete("with-" + request.getParameter());
	}

}

package com.braintribe.model.processing.service.impl.anno.processors;

import com.braintribe.model.processing.service.api.MappingServiceProcessor;
import com.braintribe.model.processing.service.api.Service;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.impl.anno.model.AnnoTestRequest;
import com.braintribe.model.processing.service.impl.anno.model.EvaluateWithContext;
import com.braintribe.model.processing.service.impl.anno.model.EvaluateWithoutContext;

/**
 * @author peter.gazdik
 */
public class AnnotatedTestProcessor implements MappingServiceProcessor<AnnoTestRequest, String> {

	@Service
	public String evalWithoutContext(EvaluateWithoutContext request) {
		return "without-" + request.getParameter();
	}

	@Service
	public String evalWithoutContext(EvaluateWithContext request, @SuppressWarnings("unused") ServiceRequestContext ctx) {
		return "with-" + request.getParameter();
	}

}

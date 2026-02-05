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
package com.braintribe.model.processing.mqrpc.client;

import java.util.Objects;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.resource.CallStreamCapture;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * <p>
 * Message queue driven GM RPC evaluator.
 * 
 * <p>
 * MQ-driven GM RPC is very limited in comparison to the HTTP-driven counterpart. The transport of binary data through
 * {@link Resource}(s) and {@link CallStreamCapture}(s) is not supported. It must be used only in cases where a
 * fan-out approach is required and for punctual requests for which the server must ensure the caller is a trusted peer
 * application.
 * 
 */
public class GmMqRpcEvaluator extends GmMqRpcClientBase implements Evaluator<ServiceRequest> {

	private static final Logger logger = Logger.getLogger(GmMqRpcEvaluator.class);

	@Override
	@Required
	@Configurable
	public void setConfig(BasicGmMqRpcClientConfig config) {
		super.setConfig(config);
		logClientConfiguration(logger, true);
	}

	@Override
	public <T> EvalContext<T> eval(ServiceRequest serviceRequest) {
		Objects.requireNonNull(serviceRequest, "serviceRequest must not be null");
		return new RpcEvalContext<T>(serviceRequest);
	}

	public static GmMqRpcEvaluator create(BasicGmMqRpcClientConfig config) {
		GmMqRpcEvaluator evaluator = new GmMqRpcEvaluator();
		evaluator.setConfig(config);
		return evaluator;
	}

	@Override
	protected Logger logger() {
		return logger;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
	}

}

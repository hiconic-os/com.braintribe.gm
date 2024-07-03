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
package com.braintribe.transport.messaging.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import com.braintribe.logging.Logger;

public class JmsExceptionListener implements ExceptionListener {

	protected Logger logger = null;

	public JmsExceptionListener(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void onException(JMSException e) {
		if (e != null) {
			logger.error(e.toString(), e);
			Exception linkedException = e.getLinkedException();
			if (linkedException != null) {
				logger.error(linkedException.toString(), e);
			}
		}
	}

}

// ============================================================================
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
package com.braintribe.model.processing.webrpc.client;

import com.braintribe.model.generic.eval.EvalContextAspect;

/**
 * If GmWebRpcClient finds this attribute it will send it as HTTP Origin header
 */
public interface HttpSendOrigin extends EvalContextAspect<String> {
	// Intentionally left blank
}
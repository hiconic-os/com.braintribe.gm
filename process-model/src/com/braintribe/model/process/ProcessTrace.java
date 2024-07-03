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
package com.braintribe.model.process;

import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Priority;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.tracing.ExceptionTrace;
import com.braintribe.model.tracing.SimpleTrace;

@SelectiveInformation("${event} [${state}]")
public interface ProcessTrace extends SimpleTrace {

	EntityType<ProcessTrace> T = EntityTypes.T(ProcessTrace.class);

	String exceptionTrace = "exceptionTrace";
	String fromState = "fromState";
	String toState = "toState";
	String state = "state";
	String details = "details";
	String msg = "msg";
	String restart = "restart";

	// @formatter:off
	@Priority(5)
	ExceptionTrace getExceptionTrace();
	void setExceptionTrace(ExceptionTrace exceptionTrace);

	@Priority(9)
	String getFromState();
	void setFromState(String fromState);

	@Priority(8)
	String getToState();
	void setToState(String toState);

	@Priority(10)
	String getState();
	void setState(String state);

	ProcessTraceDetails getDetails();
	void setDetails(ProcessTraceDetails details);

	@Priority(7)
	String getMsg();
	void setMsg(String msg);

	@Priority(6)
	boolean getRestart();
	void setRestart(boolean restart);
	// @formatter:on
}

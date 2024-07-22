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
package com.braintribe.model.execution.persistence;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum JobState implements EnumBase<JobState> {

	// "Running" states
	pending, 
	enqueued, 
	running, 
	waiting,
	
	// "Resting" (done) states
	panic, 
	done, 
	markedForRemoval, 
	notFound; 
	
	//pending: not yet ready for execution
	
	//enqueued: waiting for execution
	
	//running: running
	
	//panic: an error happened
	//  reason code: unsupported source, unsupported target, timeout, document too large, illegal call back url, illegal arguments, upload error
	//  error message:
	//  stacktrace
	
	//done:
	//  reason code: cancelled by client
	
	//markedForRemoval: will be deleted shortly
	
	//notFound: not found

	
	public static final EnumType<JobState> T = EnumTypes.T(JobState.class);
	
	@Override
	public EnumType<JobState> type() {
		return T;
	}
}

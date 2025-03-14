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

import java.util.Date;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * base class for all processes that can be handled by the process engine<br/><br/>
 * 
 * the current {@link ProcessTrace}<br/>
 * a set of {@link ProcessTrace} (all traces of the process)<br/>
 * 
 * 
 * @author Pit, Dirk
 *
 */
@Abstract
public interface Process extends GenericEntity {

	EntityType<Process> T = EntityTypes.T(Process.class);
	//property names for Query builders uses
	public static final String trace = "trace";
	public static final String traces = "traces";
	public static final String lastTransit = "lastTransit";
	public static final String overdueAt = "overdueAt";
	public static final String restartCounters = "restartCounters";
	public static final String activity = "activity";

	void setLastTransit(Date lastTransit);
	Date getLastTransit();
	
	void setOverdueAt(Date overdueAt);
	Date getOverdueAt();
	
	void setTrace( ProcessTrace trace);
	ProcessTrace getTrace();
	
	void setTraces( Set<ProcessTrace> traces);
	Set<ProcessTrace> getTraces();
	
	Set<RestartCounter> getRestartCounters();
	void setRestartCounters(Set<RestartCounter> restartCounters);
	
	ProcessActivity getActivity();
	void setActivity(ProcessActivity activity);
	
}

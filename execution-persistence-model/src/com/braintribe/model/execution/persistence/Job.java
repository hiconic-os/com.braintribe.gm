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

import java.util.Date;

import com.braintribe.model.generic.StandardStringIdentifiable;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.annotation.meta.Priority;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
@SelectiveInformation("${#type_short}")
public interface Job extends StandardStringIdentifiable {

	EntityType<Job> T = EntityTypes.T(Job.class);

	String creationTimestamp = "creationTimestamp";
	String startTimestamp = "startTimestamp";
	String endTimestamp = "endTimestamp";
	String lastStatusUpdate = "lastStatusUpdate";
	String state = "state";
	String workerId = "workerId";
	String errorMessage = "errorMessage";
	String stackTrace = "stackTrace";
	String priority = "priority";
	String notificationTimestamp = "notificationTimestamp";
	String duration = "duration";

	void setState(JobState state);
	@Priority(100d)
	@Name("State")
	@Description("The current state of the job.")
	JobState getState();

	void setCreationTimestamp(Date creationTimestamp);
	@Priority(50d)
	@Name("Creation Time")
	@Description("The creation time of the job.")
	Date getCreationTimestamp();

	void setStartTimestamp(Date startTimestamp);
	@Priority(50d)
	@Name("Start Time")
	@Description("The start time of the job.")
	Date getStartTimestamp();

	void setEndTimestamp(Date endTimestamp);
	@Priority(49d)
	@Name("End Time")
	@Description("The time the job has finished.")
	Date getEndTimestamp();

	void setDuration(Long duration);
	@Priority(48d)
	@Name("Duration (ms)")
	@Description("The time the job took for processing (in milliseconds).")
	Long getDuration();

	void setPriority(Double priority);
	@Priority(40d)
	@Name("Priority")
	@Description("The priority of the job. Jobs with higher priority will be processed first. Default is 0.")
	@Initializer("0d")
	Double getPriority();

	void setLastStatusUpdate(Date lastStatusUpdate);
	@Name("Last Update Time")
	@Description("The time of the latest job update.")
	Date getLastStatusUpdate();

	void setNotificationTimestamp(Date notificationTimestamp);
	@Name("Notification Time")
	@Description("The time the requestor has received the latest notification about this job.")
	Date getNotificationTimestamp();

	void setErrorMessage(String errorMessage);
	@Name("Error Message")
	@Description("The error message if an error occurred.")
	String getErrorMessage();

	void setStackTrace(String stackTrace);
	@Name("Error Stacktrace")
	@Description("The error stacktrace if an error occurred.")
	String getStackTrace();

	void setWorkerId(String workerId);
	@Priority(-100d)
	@Name("Worker Id")
	@Description("The Id of the worker that processed the job.")
	String getWorkerId();

}

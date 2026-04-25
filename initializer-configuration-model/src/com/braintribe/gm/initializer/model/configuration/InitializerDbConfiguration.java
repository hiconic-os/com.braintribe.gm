package com.braintribe.gm.initializer.model.configuration;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
public interface InitializerDbConfiguration extends GenericEntity {

	EntityType<InitializerDbConfiguration> T = EntityTypes.T(InitializerDbConfiguration.class);

	@Mandatory
	String getDatabaseId();
	void setDatabaseId(String databaseId);

	@Initializer("'hc_initializer_tasks'")
	@Mandatory
	String getTasksTableName();
	void setTasksTableName(String tasksTableName);

}

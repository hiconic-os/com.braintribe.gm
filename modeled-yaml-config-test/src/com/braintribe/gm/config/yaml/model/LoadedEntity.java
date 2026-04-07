package com.braintribe.gm.config.yaml.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
@ToStringInformation("LoadedEntity[${globalId}]-${origin}")
public interface LoadedEntity extends GenericEntity {

	EntityType<LoadedEntity> T = EntityTypes.T(LoadedEntity.class);

	String getOrigin();
	void setOrigin(String origin);

	String getPreCpValue();
	void setPreCpValue(String preCpValue);

	String getCpValue();
	void setCpValue(String cpValue);

	String getPreFsValue();
	void setPreFsValue(String preFsValue);

	String getFs1Value();
	void setFs1Value(String fs1Value);

	String getFs2Value();
	void setFs2Value(String fs2Value);

	String getAfterAllValue();
	void setAfterAllValue(String afterAllValue);

	boolean getPrimitiveBoolean();
	void setPrimitiveBoolean(boolean primitiveBoolean);

}

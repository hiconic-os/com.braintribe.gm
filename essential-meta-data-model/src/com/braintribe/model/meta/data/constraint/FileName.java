// ============================================================================
package com.braintribe.model.meta.data.constraint;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.PropertyMetaData;

/**
 * @see FolderName
 * 
 * @author peter.gazdik
 */
@Description("Specifies that a given property of type string denotes a file name.")
public interface FileName extends PropertyMetaData {

	EntityType<FileName> T = EntityTypes.T(FileName.class);

	boolean getMustExist();
	void setMustExist(boolean mustExist);

}

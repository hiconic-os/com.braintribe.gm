// ============================================================================
package com.braintribe.model.meta.data.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.ExplicitPredicate;
import com.braintribe.model.meta.data.ModelSkeletonCompatible;
import com.braintribe.model.meta.data.PropertyMetaData;

/** Marks a property as a version property for optimistic locking. */
public interface Version extends PropertyMetaData, ExplicitPredicate, ModelSkeletonCompatible {

	EntityType<Version> T = EntityTypes.T(Version.class);

}

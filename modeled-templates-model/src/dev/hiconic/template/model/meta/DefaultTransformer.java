package dev.hiconic.template.model.meta;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.PropertyMetaData;

/** Selects a registered conversion descriptor for a bare property output. */
public interface DefaultTransformer extends PropertyMetaData {
	EntityType<DefaultTransformer> T = EntityTypes.T(DefaultTransformer.class);
	String getTransformer();
	void setTransformer(String transformer);
}

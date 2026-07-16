package dev.hiconic.template.test.model.demo;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface Organization extends GenericEntity {
	EntityType<Organization> T = EntityTypes.T(Organization.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral website = PropertyLiteral.of(T, "website");
	PropertyLiteral contactEmail = PropertyLiteral.of(T, "contactEmail");

	String getName();
	void setName(String name);

	String getWebsite();
	void setWebsite(String website);

	String getContactEmail();
	void setContactEmail(String contactEmail);
}

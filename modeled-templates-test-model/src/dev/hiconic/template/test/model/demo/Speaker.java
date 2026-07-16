package dev.hiconic.template.test.model.demo;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface Speaker extends GenericEntity {
	EntityType<Speaker> T = EntityTypes.T(Speaker.class);

	PropertyLiteral fullName = PropertyLiteral.of(T, "fullName");
	PropertyLiteral title = PropertyLiteral.of(T, "title");
	PropertyLiteral company = PropertyLiteral.of(T, "company");
	PropertyLiteral email = PropertyLiteral.of(T, "email");
	PropertyLiteral bio = PropertyLiteral.of(T, "bio");
	PropertyLiteral expert = PropertyLiteral.of(T, "expert");
	PropertyLiteral topics = PropertyLiteral.of(T, "topics");

	String getFullName();
	void setFullName(String fullName);

	String getTitle();
	void setTitle(String title);

	String getCompany();
	void setCompany(String company);

	String getEmail();
	void setEmail(String email);

	String getBio();
	void setBio(String bio);

	boolean getExpert();
	void setExpert(boolean expert);

	Set<String> getTopics();
	void setTopics(Set<String> topics);
}

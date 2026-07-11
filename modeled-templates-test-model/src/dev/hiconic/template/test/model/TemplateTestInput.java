package dev.hiconic.template.test.model;

import java.util.Date;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface TemplateTestInput extends GenericEntity {
	EntityType<TemplateTestInput> T = EntityTypes.T(TemplateTestInput.class);

	PropertyLiteral title = PropertyLiteral.of(T, "title");
	PropertyLiteral birthday = PropertyLiteral.of(T, "birthday");
	PropertyLiteral persons = PropertyLiteral.of(T, "persons");

	String getTitle();
	void setTitle(String title);

	Date getBirthday();
	void setBirthday(Date birthday);

	List<TestPerson> getPersons();
	void setPersons(List<TestPerson> persons);
}

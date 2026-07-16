package dev.hiconic.template.test.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface TestPerson extends GenericEntity {
	EntityType<TestPerson> T = EntityTypes.T(TestPerson.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral active = PropertyLiteral.of(T, "active");
	PropertyLiteral address = PropertyLiteral.of(T, "address");
	PropertyLiteral tags = PropertyLiteral.of(T, "tags");
	PropertyLiteral friends = PropertyLiteral.of(T, "friends");
	PropertyLiteral birthday = PropertyLiteral.of(T, "birthday");

	String getName();
	void setName(String name);

	Date getBirthday();
	void setBirthday(Date birthday);
	
	boolean getActive();
	void setActive(boolean active);

	TestAddress getAddress();
	void setAddress(TestAddress address);

	List<String> getTags();
	void setTags(List<String> tags);
	
	Set<TestPerson> getFriends();
	void setFriends(Set<TestPerson> friends);
}

package dev.hiconic.template.test.model.demo;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface Track extends GenericEntity {
	EntityType<Track> T = EntityTypes.T(Track.class);

	PropertyLiteral code = PropertyLiteral.of(T, "code");
	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral color = PropertyLiteral.of(T, "color");
	PropertyLiteral children = PropertyLiteral.of(T, "children");

	String getCode();
	void setCode(String code);

	String getName();
	void setName(String name);

	String getColor();
	void setColor(String color);

	List<Track> getChildren();
	void setChildren(List<Track> children);
}

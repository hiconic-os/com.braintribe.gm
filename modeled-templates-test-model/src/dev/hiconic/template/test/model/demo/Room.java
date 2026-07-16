package dev.hiconic.template.test.model.demo;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface Room extends GenericEntity {
	EntityType<Room> T = EntityTypes.T(Room.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral floor = PropertyLiteral.of(T, "floor");
	PropertyLiteral capacity = PropertyLiteral.of(T, "capacity");

	String getName();
	void setName(String name);

	int getFloor();
	void setFloor(int floor);

	int getCapacity();
	void setCapacity(int capacity);
}

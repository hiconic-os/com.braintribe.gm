package dev.hiconic.template.test.model.demo;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface Venue extends GenericEntity {
	EntityType<Venue> T = EntityTypes.T(Venue.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral street = PropertyLiteral.of(T, "street");
	PropertyLiteral city = PropertyLiteral.of(T, "city");
	PropertyLiteral country = PropertyLiteral.of(T, "country");
	PropertyLiteral rooms = PropertyLiteral.of(T, "rooms");

	String getName();
	void setName(String name);

	String getStreet();
	void setStreet(String street);

	String getCity();
	void setCity(String city);

	String getCountry();
	void setCountry(String country);

	List<Room> getRooms();
	void setRooms(List<Room> rooms);
}

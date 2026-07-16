package dev.hiconic.template.test.model.demo;

import java.util.Date;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface TicketTier extends GenericEntity {
	EntityType<TicketTier> T = EntityTypes.T(TicketTier.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral price = PropertyLiteral.of(T, "price");
	PropertyLiteral availableUntil = PropertyLiteral.of(T, "availableUntil");
	PropertyLiteral includesWorkshop = PropertyLiteral.of(T, "includesWorkshop");
	PropertyLiteral includedTracks = PropertyLiteral.of(T, "includedTracks");

	String getName();
	void setName(String name);

	double getPrice();
	void setPrice(double price);

	Date getAvailableUntil();
	void setAvailableUntil(Date availableUntil);

	boolean getIncludesWorkshop();
	void setIncludesWorkshop(boolean includesWorkshop);

	Set<Track> getIncludedTracks();
	void setIncludedTracks(Set<Track> includedTracks);
}

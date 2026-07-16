package dev.hiconic.template.test.model.demo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface Conference extends GenericEntity {
	EntityType<Conference> T = EntityTypes.T(Conference.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral tagline = PropertyLiteral.of(T, "tagline");
	PropertyLiteral publishedAt = PropertyLiteral.of(T, "publishedAt");
	PropertyLiteral organizer = PropertyLiteral.of(T, "organizer");
	PropertyLiteral venue = PropertyLiteral.of(T, "venue");
	PropertyLiteral tracks = PropertyLiteral.of(T, "tracks");
	PropertyLiteral speakers = PropertyLiteral.of(T, "speakers");
	PropertyLiteral sessions = PropertyLiteral.of(T, "sessions");
	PropertyLiteral ticketTiers = PropertyLiteral.of(T, "ticketTiers");
	PropertyLiteral facts = PropertyLiteral.of(T, "facts");

	String getName();
	void setName(String name);

	String getTagline();
	void setTagline(String tagline);

	Date getPublishedAt();
	void setPublishedAt(Date publishedAt);

	Organization getOrganizer();
	void setOrganizer(Organization organizer);

	Venue getVenue();
	void setVenue(Venue venue);

	List<Track> getTracks();
	void setTracks(List<Track> tracks);

	Set<Speaker> getSpeakers();
	void setSpeakers(Set<Speaker> speakers);

	List<ConferenceSession> getSessions();
	void setSessions(List<ConferenceSession> sessions);

	List<TicketTier> getTicketTiers();
	void setTicketTiers(List<TicketTier> ticketTiers);

	Map<String, String> getFacts();
	void setFacts(Map<String, String> facts);
}

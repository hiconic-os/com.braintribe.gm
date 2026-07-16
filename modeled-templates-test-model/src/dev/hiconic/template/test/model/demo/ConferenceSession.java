package dev.hiconic.template.test.model.demo;

import java.util.Date;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface ConferenceSession extends GenericEntity {
	EntityType<ConferenceSession> T = EntityTypes.T(ConferenceSession.class);

	PropertyLiteral title = PropertyLiteral.of(T, "title");
	PropertyLiteral abstractText = PropertyLiteral.of(T, "abstractText");
	PropertyLiteral startsAt = PropertyLiteral.of(T, "startsAt");
	PropertyLiteral durationMinutes = PropertyLiteral.of(T, "durationMinutes");
	PropertyLiteral kind = PropertyLiteral.of(T, "kind");
	PropertyLiteral level = PropertyLiteral.of(T, "level");
	PropertyLiteral featured = PropertyLiteral.of(T, "featured");
	PropertyLiteral rating = PropertyLiteral.of(T, "rating");
	PropertyLiteral speaker = PropertyLiteral.of(T, "speaker");
	PropertyLiteral coSpeakers = PropertyLiteral.of(T, "coSpeakers");
	PropertyLiteral track = PropertyLiteral.of(T, "track");
	PropertyLiteral room = PropertyLiteral.of(T, "room");
	PropertyLiteral tags = PropertyLiteral.of(T, "tags");

	String getTitle();
	void setTitle(String title);

	String getAbstractText();
	void setAbstractText(String abstractText);

	Date getStartsAt();
	void setStartsAt(Date startsAt);

	int getDurationMinutes();
	void setDurationMinutes(int durationMinutes);

	SessionKind getKind();
	void setKind(SessionKind kind);

	SessionLevel getLevel();
	void setLevel(SessionLevel level);

	boolean getFeatured();
	void setFeatured(boolean featured);

	double getRating();
	void setRating(double rating);

	Speaker getSpeaker();
	void setSpeaker(Speaker speaker);

	List<Speaker> getCoSpeakers();
	void setCoSpeakers(List<Speaker> coSpeakers);

	Track getTrack();
	void setTrack(Track track);

	Room getRoom();
	void setRoom(Room room);

	List<String> getTags();
	void setTags(List<String> tags);
}

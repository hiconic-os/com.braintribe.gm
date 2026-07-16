package dev.hiconic.template.docs;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.hiconic.template.test.model.demo.Conference;
import dev.hiconic.template.test.model.demo.ConferenceSession;
import dev.hiconic.template.test.model.demo.Organization;
import dev.hiconic.template.test.model.demo.Room;
import dev.hiconic.template.test.model.demo.SessionKind;
import dev.hiconic.template.test.model.demo.SessionLevel;
import dev.hiconic.template.test.model.demo.Speaker;
import dev.hiconic.template.test.model.demo.TicketTier;
import dev.hiconic.template.test.model.demo.Track;
import dev.hiconic.template.test.model.demo.Venue;

public final class ConferenceDemoData {
	private ConferenceDemoData() {
	}

	public static Conference createConference() {
		Organization organizer = organization("Hiconic Labs", "https://hiconic.dev", "hello@hiconic.dev");

		Room mainHall = room("Main Hall", 0, 420);
		Room workshopRoom = room("Workshop Studio", 1, 80);
		Room forum = room("Model Forum", 2, 140);
		Venue venue = venue("Riverside Technology Forum", "Rheinpromenade 7", "Cologne", "Germany",
				list(mainHall, workshopRoom, forum));

		Track modeling = track("MOD", "Reflective Modeling", "#7C3AED",
				track("MOD-GM", "GM Meta Models", "#8B5CF6"),
				track("MOD-MD", "Metadata and Constraints", "#A78BFA"));
		Track reactive = track("PAI", "Reactive Evaluation", "#0EA5E9",
				track("PAI-VD", "Value Descriptors", "#38BDF8"),
				track("PAI-SCOPE", "Evaluation Scope", "#67E8F9"));
		Track delivery = track("DEL", "Delivery Systems", "#F97316",
				track("DEL-HTML", "Safe HTML Output", "#FB923C"),
				track("DEL-DOCS", "Executable Documentation", "#FDBA74"));

		Speaker ada = speaker("Ada Krämer", "Principal Model Architect", "Hiconic Labs",
				"ada.kraemer@hiconic.dev",
				"Builds normalized domain models that stay useful after parsing, transport and evaluation.",
				true, set("GM", "reflection", "type-safety"));
		Speaker noah = speaker("Noah Singh", "Runtime Engineer", "Flowgrid",
				"noah.singh@flowgrid.example",
				"Works on reactive property access and predictable evaluation pipelines.",
				true, set("PAI", "streaming", "evaluation"));
		Speaker lena = speaker("Lena Ortiz", "Developer Experience Lead", "Northwind Cloud",
				"lena.ortiz@northwind.example",
				"Turns strict model semantics into approachable authoring experiences.",
				false, set("templates", "documentation", "developer-experience"));

		List<ConferenceSession> sessions = list(
				session("Models as an Exo-Type System",
						"Why reflected models can act as a type system outside the programming language.",
						"2026-09-18T09:00:00Z", 50, SessionKind.keynote, SessionLevel.advanced, true, 4.9,
						ada, list(noah), modeling, mainHall, list("exo-types", "reflection", "GM")),
				session("Reactive Property Access in Practice",
						"How PAI turns object graphs and value descriptors into evaluable model structures.",
						"2026-09-18T10:15:00Z", 45, SessionKind.talk, SessionLevel.intermediate, true, 4.7,
						noah, list(), reactive, forum, list("PAI", "VD", "evaluation")),
				session("Authoring Safe HTML Templates",
						"A workshop on typed paths, declared instructions and explicit output safety.",
						"2026-09-18T13:30:00Z", 120, SessionKind.workshop, SessionLevel.introductory, false, 4.5,
						lena, list(ada), delivery, workshopRoom, list("HTML", "SafeOutput", "escaping")));

		List<TicketTier> tickets = list(
				ticket("Community", 99.0, "2026-08-01T23:59:00Z", false, set(modeling, reactive)),
				ticket("Professional", 249.0, "2026-09-01T23:59:00Z", true, set(modeling, reactive, delivery)));

		Map<String, String> facts = new LinkedHashMap<>();
		facts.put("timezone", "Europe/Berlin");
		facts.put("format", "single-day conference");
		facts.put("audience", "model-driven engineers");

		Conference conference = Conference.T.create();
		conference.setName("Hiconic Model Templates Summit");
		conference.setTagline("A small conference about models that remain alive at runtime.");
		conference.setPublishedAt(date("2026-07-15T08:00:00Z"));
		conference.setOrganizer(organizer);
		conference.setVenue(venue);
		conference.setTracks(list(modeling, reactive, delivery));
		conference.setSpeakers(set(ada, noah, lena));
		conference.setSessions(sessions);
		conference.setTicketTiers(tickets);
		conference.setFacts(facts);
		return conference;
	}

	private static Organization organization(String name, String website, String email) {
		Organization organization = Organization.T.create();
		organization.setName(name);
		organization.setWebsite(website);
		organization.setContactEmail(email);
		return organization;
	}

	private static Venue venue(String name, String street, String city, String country, List<Room> rooms) {
		Venue venue = Venue.T.create();
		venue.setName(name);
		venue.setStreet(street);
		venue.setCity(city);
		venue.setCountry(country);
		venue.setRooms(rooms);
		return venue;
	}

	private static Room room(String name, int floor, int capacity) {
		Room room = Room.T.create();
		room.setName(name);
		room.setFloor(floor);
		room.setCapacity(capacity);
		return room;
	}

	private static Track track(String code, String name, String color, Track... children) {
		Track track = Track.T.create();
		track.setCode(code);
		track.setName(name);
		track.setColor(color);
		track.setChildren(list(children));
		return track;
	}

	private static Speaker speaker(String fullName, String title, String company, String email, String bio,
			boolean expert, Set<String> topics) {
		Speaker speaker = Speaker.T.create();
		speaker.setFullName(fullName);
		speaker.setTitle(title);
		speaker.setCompany(company);
		speaker.setEmail(email);
		speaker.setBio(bio);
		speaker.setExpert(expert);
		speaker.setTopics(topics);
		return speaker;
	}

	private static ConferenceSession session(String title, String abstractText, String startsAt, int durationMinutes,
			SessionKind kind, SessionLevel level, boolean featured, double rating, Speaker speaker,
			List<Speaker> coSpeakers, Track track, Room room, List<String> tags) {
		ConferenceSession session = ConferenceSession.T.create();
		session.setTitle(title);
		session.setAbstractText(abstractText);
		session.setStartsAt(date(startsAt));
		session.setDurationMinutes(durationMinutes);
		session.setKind(kind);
		session.setLevel(level);
		session.setFeatured(featured);
		session.setRating(rating);
		session.setSpeaker(speaker);
		session.setCoSpeakers(coSpeakers);
		session.setTrack(track);
		session.setRoom(room);
		session.setTags(tags);
		return session;
	}

	private static TicketTier ticket(String name, double price, String availableUntil, boolean includesWorkshop,
			Set<Track> includedTracks) {
		TicketTier ticket = TicketTier.T.create();
		ticket.setName(name);
		ticket.setPrice(price);
		ticket.setAvailableUntil(date(availableUntil));
		ticket.setIncludesWorkshop(includesWorkshop);
		ticket.setIncludedTracks(includedTracks);
		return ticket;
	}

	private static Date date(String instant) {
		return Date.from(Instant.parse(instant));
	}

	@SafeVarargs
	private static <T> List<T> list(T... values) {
		List<T> result = new ArrayList<>();
		for (T value : values)
			result.add(value);
		return result;
	}

	@SafeVarargs
	private static <T> Set<T> set(T... values) {
		Set<T> result = new LinkedHashSet<>();
		for (T value : values)
			result.add(value);
		return result;
	}
}

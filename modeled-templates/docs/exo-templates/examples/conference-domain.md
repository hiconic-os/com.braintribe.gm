# Example Domain - Conference

The documentation examples use a small but real-world-shaped conference model.

The root type is:

```java
dev.hiconic.template.test.model.demo.Conference
```

It contains:

- strings: `name`, `tagline`;
- dates: `publishedAt`, session start times, ticket deadlines;
- booleans: `Speaker.expert`, `ConferenceSession.featured`, `TicketTier.includesWorkshop`;
- numbers: room capacity, duration minutes, session rating, ticket price;
- enums: `SessionKind`, `SessionLevel`;
- lists: tracks, sessions, ticket tiers, rooms, tags;
- sets: speakers, speaker topics, included ticket tracks;
- maps: conference facts;
- entity references: sessions point to speaker, track and room entities.
- recursion: `Track.children` creates a nested program map.

The model is intentionally connected rather than flat. For example, a `ConferenceSession` references a `Speaker`, a `Track` and a `Room`. This allows templates to demonstrate typed property paths such as:

```hiconic-template
${session.speaker.fullName}
${session.room.name}
${session.track.name}
```

The deterministic generator is `ConferenceDemoData.createConference()`. The documentation tests render the same data through all example templates.

## Why this domain

A conference is small enough to understand immediately, but rich enough to exercise the template system:

- multiple entity types;
- nested references;
- collections;
- enums;
- formatting;
- conditional rendering;
- reusable declared instructions;
- recursive declared instruction calls;
- safe HTML output.

%(declare-instruction session-card {session ConferenceSession})
	#{Reusable card for a typed ConferenceSession parameter}
	<article class="session">
		<h2>${session.title}</h2>
		<p class="meta">
			${session.startsAt | (format-date "HH:mm")} · ${session.durationMinutes} min ·
			${session.room.name} · ${session.track.name}
		</p>
		<p class="speaker">${session.speaker.fullName}, ${session.speaker.company}</p>
		%(switch session.kind)
			%(when (eq session.kind SessionKind::keynote))
				<p class="badge">Opening keynote</p>
			%(when session.featured)
				<p class="badge">Featured session</p>
			%(default)
				<p class="badge">Track session</p>
			%(end)
		<ul class="tags">
			%(for-each session.tags tag)
				<li>${tag}</li>
			%(end)
		</ul>
	</article>
%(end)

%(declare-instruction track-node {track Track} {depth integer})
	#{Recursive rendering: each child Track is rendered by this same instruction}
	<li>
		<span class="track" data-depth="${depth}">${track.code}: ${track.name}</span>
		%(if (gt (size track.children) 0))
			#{The reflected Track.children type lets the parser validate the recursive call}
			<ul>
				%(for-each track.children child)
					%(track-node child (add depth 1))
				%(end)
			</ul>
		%(end)
	</li>
%(end)

<html>
	<body>
		#{All paths below are validated against the Conference root type}
		<h1>${input.name}</h1>
		<p>${input.tagline}</p>
		<p>
			Organized by ${input.organizer.name} at ${input.venue.name},
			${input.venue.city}, ${input.venue.country}.
		</p>
		<p>Published ${input.publishedAt | (format-date "yyyy-MM-dd")}</p>
		<nav class="track-map">
			<h2>Program map</h2>
			<ul>
				%(for-each input.tracks track)
					%(track-node track 0)
				%(end)
			</ul>
		</nav>
		<section>
			%(for-each input.sessions session)
				%(session-card session)
			%(end)
		</section>
	</body>
</html>

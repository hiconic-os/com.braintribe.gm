# Example - Conference Agenda

Resource:

```text
modeled-templates-test/res/docs/conference-agenda.html.mt
```

Test:

```text
ModeledTemplateDocumentationExamplesTest.rendersConferenceAgendaExample
```

## What it demonstrates

- root type `Conference`;
- property paths over connected entities;
- declared instruction `session-card`;
- recursive declared instruction `track-node`;
- lexical parameter scope;
- `for-each` over sessions, tags and recursive child tracks;
- `switch` with `when` and `default` clauses;
- enum literal `SessionKind::keynote`;
- `format-date`;
- `size`, `gt` and `add` value descriptors;
- automatic HTML-safe output.

## 1. Data

The rendered data is produced by `ConferenceDemoData.createConference()`.

Relevant shape:

```text
Conference
  name: Hiconic Model Templates Summit
  publishedAt: 2026-07-15T08:00:00Z
  tracks:
    MOD: Reflective Modeling
      MOD-GM: GM Meta Models
      MOD-MD: Metadata and Constraints
    PAI: Reactive Evaluation
      PAI-VD: Value Descriptors
      PAI-SCOPE: Evaluation Scope
    DEL: Delivery Systems
      DEL-HTML: Safe HTML Output
      DEL-DOCS: Executable Documentation
  sessions:
    Models as an Exo-Type System
    Reactive Property Access in Practice
    Authoring Safe HTML Templates
```

The recursive part is `Track.children`.

## 2. Template code

<pre style="background:#111827;color:#e5e7eb;padding:1rem;border-radius:8px;overflow:auto;"><code><span style="color:#facc15">%(declare-instruction</span> <span style="color:#93c5fd">track-node</span> <span style="color:#f472b6">{track Track}</span> <span style="color:#f472b6">{depth integer}</span><span style="color:#facc15">)</span>
	<span style="color:#6ee7b7">#{Recursive rendering: each child Track is rendered by this same instruction}</span>
	<span style="color:#60a5fa">&lt;li&gt;</span>
		<span style="color:#60a5fa">&lt;span</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"track"</span> <span style="color:#c4b5fd">data-depth</span>=<span style="color:#fca5a5">"</span><span style="color:#34d399">${depth}</span><span style="color:#fca5a5">"</span><span style="color:#60a5fa">&gt;</span><span style="color:#34d399">${track.code}</span>: <span style="color:#34d399">${track.name}</span><span style="color:#60a5fa">&lt;/span&gt;</span>
		<span style="color:#facc15">%(if</span> <span style="color:#a78bfa">(gt (size track.children) 0)</span><span style="color:#facc15">)</span>
			<span style="color:#6ee7b7">#{The reflected Track.children type lets the parser validate the recursive call}</span>
			<span style="color:#60a5fa">&lt;ul&gt;</span>
				<span style="color:#facc15">%(for-each</span> <span style="color:#93c5fd">track.children</span> <span style="color:#93c5fd">child</span><span style="color:#facc15">)</span>
					<span style="color:#facc15">%(</span><span style="color:#93c5fd">track-node</span> child <span style="color:#a78bfa">(add depth 1)</span><span style="color:#facc15">)</span>
				<span style="color:#facc15">%(end)</span>
			<span style="color:#60a5fa">&lt;/ul&gt;</span>
		<span style="color:#facc15">%(end)</span>
	<span style="color:#60a5fa">&lt;/li&gt;</span>
<span style="color:#facc15">%(end)</span>

<span style="color:#60a5fa">&lt;nav</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"track-map"</span><span style="color:#60a5fa">&gt;</span>
	<span style="color:#60a5fa">&lt;h2&gt;</span>Program map<span style="color:#60a5fa">&lt;/h2&gt;</span>
	<span style="color:#60a5fa">&lt;ul&gt;</span>
		<span style="color:#facc15">%(for-each</span> <span style="color:#93c5fd">input.tracks</span> <span style="color:#93c5fd">track</span><span style="color:#facc15">)</span>
			<span style="color:#facc15">%(</span><span style="color:#93c5fd">track-node</span> track <span style="color:#fca5a5">0</span><span style="color:#facc15">)</span>
		<span style="color:#facc15">%(end)</span>
	<span style="color:#60a5fa">&lt;/ul&gt;</span>
<span style="color:#60a5fa">&lt;/nav&gt;</span>

<span style="color:#facc15">%(declare-instruction</span> <span style="color:#93c5fd">session-card</span> <span style="color:#f472b6">{session ConferenceSession}</span><span style="color:#facc15">)</span>
	<span style="color:#6ee7b7">#{Reusable card for a typed ConferenceSession parameter}</span>
	<span style="color:#60a5fa">&lt;article</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"session"</span><span style="color:#60a5fa">&gt;</span>
		<span style="color:#60a5fa">&lt;h2&gt;</span><span style="color:#34d399">${session.title}</span><span style="color:#60a5fa">&lt;/h2&gt;</span>
		<span style="color:#60a5fa">&lt;p</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"meta"</span><span style="color:#60a5fa">&gt;</span>
			<span style="color:#34d399">${session.startsAt | (format-date "HH:mm")}</span> &middot; <span style="color:#34d399">${session.durationMinutes}</span> min &middot;
			<span style="color:#34d399">${session.room.name}</span> &middot; <span style="color:#34d399">${session.track.name}</span>
		<span style="color:#60a5fa">&lt;/p&gt;</span>
		<span style="color:#facc15">%(switch</span> <span style="color:#93c5fd">session.kind</span><span style="color:#facc15">)</span>
			<span style="color:#facc15">%(when</span> <span style="color:#a78bfa">(eq session.kind SessionKind::keynote)</span><span style="color:#facc15">)</span>
				<span style="color:#60a5fa">&lt;p</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"badge"</span><span style="color:#60a5fa">&gt;</span>Opening keynote<span style="color:#60a5fa">&lt;/p&gt;</span>
			<span style="color:#facc15">%(when</span> <span style="color:#93c5fd">session.featured</span><span style="color:#facc15">)</span>
				<span style="color:#60a5fa">&lt;p</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"badge"</span><span style="color:#60a5fa">&gt;</span>Featured session<span style="color:#60a5fa">&lt;/p&gt;</span>
			<span style="color:#facc15">%(default)</span>
				<span style="color:#60a5fa">&lt;p</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"badge"</span><span style="color:#60a5fa">&gt;</span>Track session<span style="color:#60a5fa">&lt;/p&gt;</span>
			<span style="color:#facc15">%(end)</span>
	<span style="color:#60a5fa">&lt;/article&gt;</span>
<span style="color:#facc15">%(end)</span></code></pre>

The time `11:00` in the rendered output comes from a UTC `Date` rendered with the configured default zone `Europe/Berlin`. Date formatting is therefore not just string formatting; it consults modeled/defaulted evaluation context.

## 3. Output code

```html
<h1>Hiconic Model Templates Summit</h1>
<nav class="track-map">
	<h2>Program map</h2>
	<ul>
		<li>
			<span class="track" data-depth="0">MOD: Reflective Modeling</span>
			<ul>
				<li><span class="track" data-depth="1">MOD-GM: GM Meta Models</span></li>
				<li><span class="track" data-depth="1">MOD-MD: Metadata and Constraints</span></li>
			</ul>
		</li>
	</ul>
</nav>
<h2>Models as an Exo-Type System</h2>
11:00 · 50 min ·
<p class="badge">Opening keynote</p>
<p class="badge">Featured session</p>
<p class="badge">Track session</p>
<li>SafeOutput</li>
```

## 4. Preview

<div style="border:1px solid #d1d5db;border-radius:8px;padding:1rem;background:#ffffff;color:#111827;">
	<section>
		<h1>Hiconic Model Templates Summit</h1>
		<nav class="track-map">
			<h2>Program map</h2>
			<ul>
				<li>
					<span class="track" data-depth="0">MOD: Reflective Modeling</span>
					<ul>
						<li><span class="track" data-depth="1">MOD-GM: GM Meta Models</span></li>
						<li><span class="track" data-depth="1">MOD-MD: Metadata and Constraints</span></li>
					</ul>
				</li>
			</ul>
		</nav>
		<article class="session">
			<h2>Models as an Exo-Type System</h2>
			<p class="meta">11:00 · 50 min · Main Hall · Reflective Modeling</p>
			<p class="badge">Opening keynote</p>
		</article>
	</section>
</div>

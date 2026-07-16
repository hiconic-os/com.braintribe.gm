# Example - Speaker Catalog

Resource:

```text
modeled-templates-test/res/docs/speaker-catalog.html.mt
```

Test:

```text
ModeledTemplateDocumentationExamplesTest.rendersSpeakerCatalogExampleWithEscapedHtmlOutput
```

## What it demonstrates

- `for-each` over a `Set<Speaker>`;
- declared instruction with a `set<string>` parameter;
- named argument passing to avoid variadic collection interpretation;
- `if`/`else`;
- HTML-safe rendering of normal string properties.

## 1. Data

The rendered data is the `Conference.speakers` set:

```text
Ada Krämer
  expert: true
  topics: GM, reflection, type-safety
Noah Singh
  expert: true
  topics: PAI, streaming, evaluation
Lena Ortiz
  expert: false
  topics: templates, documentation, developer-experience
```

## 2. Template code

<pre style="background:#111827;color:#e5e7eb;padding:1rem;border-radius:8px;overflow:auto;"><code><span style="color:#facc15">%(declare-instruction</span> <span style="color:#93c5fd">topics</span> <span style="color:#f472b6">{values set&lt;string&gt;}</span><span style="color:#facc15">)</span>
	<span style="color:#6ee7b7">#{The declared parameter is a real set&lt;string&gt;, not an untyped helper argument}</span>
	<span style="color:#60a5fa">&lt;ul</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"topics"</span><span style="color:#60a5fa">&gt;</span>
		<span style="color:#facc15">%(for-each</span> <span style="color:#93c5fd">values</span> <span style="color:#93c5fd">topic</span><span style="color:#facc15">)</span>
			<span style="color:#60a5fa">&lt;li&gt;</span><span style="color:#34d399">${topic}</span><span style="color:#60a5fa">&lt;/li&gt;</span>
		<span style="color:#facc15">%(end)</span>
	<span style="color:#60a5fa">&lt;/ul&gt;</span>
<span style="color:#facc15">%(end)</span>

<span style="color:#60a5fa">&lt;section</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"speakers"</span><span style="color:#60a5fa">&gt;</span>
	<span style="color:#60a5fa">&lt;h1&gt;</span>Speakers for <span style="color:#34d399">${input.name}</span><span style="color:#60a5fa">&lt;/h1&gt;</span>
	<span style="color:#facc15">%(for-each</span> <span style="color:#93c5fd">input.speakers</span> <span style="color:#93c5fd">speaker</span><span style="color:#facc15">)</span>
		<span style="color:#6ee7b7">#{speaker is lexically scoped and typed as Speaker inside this block}</span>
		<span style="color:#60a5fa">&lt;article</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"speaker"</span><span style="color:#60a5fa">&gt;</span>
			<span style="color:#60a5fa">&lt;h2&gt;</span><span style="color:#34d399">${speaker.fullName}</span><span style="color:#60a5fa">&lt;/h2&gt;</span>
			<span style="color:#60a5fa">&lt;p&gt;</span><span style="color:#34d399">${speaker.title}</span> at <span style="color:#34d399">${speaker.company}</span><span style="color:#60a5fa">&lt;/p&gt;</span>
			<span style="color:#facc15">%(if</span> <span style="color:#93c5fd">speaker.expert</span><span style="color:#facc15">)</span>
				<span style="color:#6ee7b7">#{Boolean property access is validated from the reflected Speaker type}</span>
				<span style="color:#60a5fa">&lt;p</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"marker"</span><span style="color:#60a5fa">&gt;</span>Recognized field expert<span style="color:#60a5fa">&lt;/p&gt;</span>
			<span style="color:#facc15">%(else)</span>
				<span style="color:#60a5fa">&lt;p</span> <span style="color:#c4b5fd">class</span>=<span style="color:#fca5a5">"marker"</span><span style="color:#60a5fa">&gt;</span>Community contributor<span style="color:#60a5fa">&lt;/p&gt;</span>
			<span style="color:#facc15">%(end)</span>
			<span style="color:#34d399">${speaker.bio}</span>
			<span style="color:#facc15">%(</span><span style="color:#93c5fd">topics</span> values: speaker.topics<span style="color:#facc15">)</span>
		<span style="color:#60a5fa">&lt;/article&gt;</span>
	<span style="color:#facc15">%(end)</span>
<span style="color:#60a5fa">&lt;/section&gt;</span></code></pre>

The named call `values: speaker.topics` is intentional. Positional calls to a collection-typed last argument are variadic, so a named argument is the clearest way to pass an existing collection as one value.

## 3. Output code

```html
<h1>Speakers for Hiconic Model Templates Summit</h1>
<h2>Ada Krämer</h2>
<p class="marker">Recognized field expert</p>
<p class="marker">Community contributor</p>
<li>developer-experience</li>
```

## 4. Preview

<div style="border:1px solid #d1d5db;border-radius:8px;padding:1rem;background:#ffffff;color:#111827;">
	<section class="speakers">
		<h1>Speakers for Hiconic Model Templates Summit</h1>
		<article class="speaker">
			<h2>Ada Krämer</h2>
			<p>Principal Model Architect at Hiconic Labs</p>
			<p class="marker">Recognized field expert</p>
			<ul class="topics">
				<li>GM</li>
				<li>reflection</li>
				<li>type-safety</li>
			</ul>
		</article>
		<article class="speaker">
			<h2>Lena Ortiz</h2>
			<p class="marker">Community contributor</p>
			<ul class="topics">
				<li>developer-experience</li>
			</ul>
		</article>
	</section>
</div>

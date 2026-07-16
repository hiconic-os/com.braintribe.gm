%(declare-instruction topics {values set<string>})
	#{The declared parameter is a real set<string>, not an untyped helper argument}
	<ul class="topics">
		%(for-each values topic)
			<li>${topic}</li>
		%(end)
	</ul>
%(end)

<section class="speakers">
	<h1>Speakers for ${input.name}</h1>
	%(for-each input.speakers speaker)
		#{speaker is lexically scoped and typed as Speaker inside this block}
		<article class="speaker">
			<h2>${speaker.fullName}</h2>
			<p>${speaker.title} at ${speaker.company}</p>
			%(if speaker.expert)
				#{Boolean property access is validated from the reflected Speaker type}
				<p class="marker">Recognized field expert</p>
			%(else)
				<p class="marker">Community contributor</p>
			%(end)
			${speaker.bio}
			%(topics values: speaker.topics)
	</article>
	%(end)
</section>

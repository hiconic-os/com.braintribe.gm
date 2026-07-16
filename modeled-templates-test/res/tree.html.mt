
(Person "Tina")
%(var p Person value: {"Tina"})


%(declare-instruction node {person TestPerson} {depth integer})
	<div>
		<label>${person.name} ${depth}</label>
		#{Rekursive Invocation}
		%(for-each person.friends friend)
			%(node friend (add depth 1))
		%(end)
	</div>
%(end)

<html>
	%(var depth integer value: 0)
	<body>
		%(set depth (add depth 2))
		%(node input depth)
	</body>
</html>

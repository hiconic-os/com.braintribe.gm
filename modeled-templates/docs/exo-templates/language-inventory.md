# Language Inventory

This is a compact inventory of the currently relevant language surface.

## Output

```hiconic-template
${input.name}
${input.publishedAt | (format-date "yyyy-MM-dd")}
${input.price | (format-number "0.00")}
```

Output expressions must evaluate to `SafeOutput`. Standard factories add default conversions such as string to HTML-safe output.

## Comments

```hiconic-template
#{This comment is modeled but silent during rendering}
```

## Variables

```hiconic-template
%(var greeting string value: "Hello")
%(var inferred value: input.name)
${greeting} ${inferred}
```

Variables are lexically scoped. Nested blocks see outer variables, but local variables do not leak out.

## Assignment

```hiconic-template
%(set localName "Ada")
%(set person.address.city "Vienna")
```

Root inputs are readonly; mutable local variables and writable property paths can be assigned.

## Conditions

```hiconic-template
%(if speaker.expert)
	Expert
%(else)
	Contributor
%(end)
```

## Loops

```hiconic-template
%(for-each input.sessions session)
	${session.title}
%(end)

%(while (lt index 10))
	%(set index (add index 1))
%(end)

%(repeat 3)
	Again
%(end)
```

Loops can support `break` and `continue` through modeled block capabilities.

## Switch clauses

```hiconic-template
%(switch session.kind)
	%(when (eq session.kind SessionKind::keynote))
		Keynote
	%(default)
		Other
	%(end)
```

`when`, `case` and `default` are modeled clause entities. `when` accepts a condition. `case` accepts a value. `default` accepts only a block.

## Declared instructions

```hiconic-template
%(declare-instruction session-card {session ConferenceSession})
	<article>${session.title}</article>
%(end)

%(session-card input.sessions[0])
```

Declared instructions are runtime-typed and validated at parse time.

Declared instructions may call themselves, as long as the runtime data eventually terminates the recursion:

```hiconic-template
%(declare-instruction track-node {track Track} {depth integer})
	<li>
		${track.name}
		%(for-each track.children child)
			%(track-node child (add depth 1))
		%(end)
	</li>
%(end)
```

## Value descriptors

Examples include:

- boolean: `and`, `or`, `not`, `eq`, `ne`, `gt`, `ge`, `lt`, `le`;
- arithmetic: `add`, `subtract`, `multiply`, `divide`;
- strings: `concat`, `like`;
- collections: `size`, `contains`, `contains-key`, `contains-value`;
- typing: `type`, `type-of`, `declared-type-of`, `is`, `assignable-to`;
- conversions: `html-esc`, `xml-escape`, `json-escape`, `css-escape`, `url-component-escape`, `java-literal-escape`, `format-date`, `format-number`, `no-escape`.

Value descriptors are ordinary model entities with experts.

## Literals

Supported literals include:

- strings with escape sequences;
- numbers with typed suffix behavior;
- booleans;
- null;
- enum literals using `Type::constant`;
- lists, sets and maps;
- entity literals using Lisp-like modeled construction syntax;
- type literals such as `(type list<string>)`.

## Whitespace

Block directives can strip structural directive-only whitespace. Declared instruction bodies can be indented like programming-language blocks and then rendered relative to the call site. Whitespace is also modeled enough to become programmable policy rather than an invisible parser side effect.

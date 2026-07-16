# Open Ends

This system is already useful and tested, but several design edges remain intentionally visible.

## Pipe authoring syntax

The current pipeline stage syntax uses explicit modeled entity syntax:

```hiconic-template
${input.publishedAt | (format-date "yyyy-MM-dd")}
```

A more natural top-level pipe form is desirable:

```hiconic-template
${input.publishedAt | format-date "yyyy-MM-dd"}
```

That should be implemented by making transformer parsing fully reflection-driven, not by adding one-off parser special cases.

## Text and Markdown factories

The examples currently use the HTML factory to demonstrate safe output. A dedicated text or Markdown factory could provide different default output conversions and escaping rules.

## Modeled defaults and JVM type caches

Defaults such as locale, zone, date pattern and number pattern are modeled, but JVM types like `Locale` and `ZoneId` are not GM types. A refined transient-property cache on modeled defaults could preserve transportability while avoiding repeated loose lookups.

## Whitespace policy

Directive-only whitespace, declared instruction indentation and call-site reindentation work, but richer policy documentation and tooling support are still needed. Important future work:

- exact control for preserving author indentation as output;
- clearer policy names for PL-like indentation;
- streaming-friendly block indentation behavior.

## Clause polymorphism

`Switch` now uses modeled clause entities (`When`, `Case`, `Default`). The next step is custom clause subtype discovery through the model oracle for user-defined clause-supportive instructions.

## Documentation generation

The examples are tested, but the Markdown output excerpts are still copied manually. A future documentation build could render examples and inject verified snippets automatically.

## Operator syntax

The system currently favors modeled VD entity calls such as `(add a b)` over infix operators. Infix syntax should only be added if operators and precedence are themselves modeled and announced by the relevant types/experts.

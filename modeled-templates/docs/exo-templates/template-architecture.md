# Template Architecture

Modeled templates are an application of the hiconic exo-type-system. The template engine uses reflected models to turn a usually stringly typed problem into a typed, inspectable and evaluable model pipeline.

The template system has three major layers:

1. modeled syntax and semantics;
2. parser/completion/validation;
3. evaluation through experts.

## Model layer

The model contains first-class entities for the template tree:

- text and sequence nodes;
- output nodes;
- comments;
- directive nodes;
- block nodes;
- declared instructions;
- runtime instruction argument specifications;
- value descriptors;
- output transformers and safe output types;
- path access segments;
- parse source ranges and parse errors.

This means a parsed template is a model tree, not an implementation-private parse tree. That is the key architectural move: once parsed, templates can participate in the same reflected model world as domain data, value descriptors and metadata.

## Parser layer

The parser reads template text and produces `TemplateNode` instances. It also performs completion and validation:

- expressions are resolved to typed `ValueDescriptor` graphs;
- property paths are checked against `GenericModelType`;
- instruction calls are reflected against modeled properties;
- declared instructions build a runtime argument shape;
- block scopes declare variables lexically;
- output expressions are completed to `SafeOutput` through transformer chains;
- parse failures produce `Reason` values with `TextRange`.

Many of these validations happen without runtime data. The parser only needs the reflected root type and the expert model space. This is the exo-type-system doing real work at a system boundary.

The parser can be strict or lenient. In lenient modes, invalid fragments can be substituted by modeled `ErrorNode`s so a consumer can still render a diagnostic template.

## Evaluation layer

Evaluation is delegated to experts:

- `TemplateNodeEvaluator<N>` evaluates and validates template nodes.
- `VdEvaluator<V, O>` evaluates and completes value descriptors.
- `ValueConversion<I, V, O>` implements transformer stages.
- `TemplateExpertRegistry` binds modeled types to their experts.

The registry is deliberately separated from the model. Models describe shape; experts provide behavior.

## Declared instructions

Declared instructions are user-defined template functions:

```hiconic-template
%(declare-instruction card {title string})
	<section>${title}</section>
%(end)

%(card "Hello")
```

They are not backed by generated Java entity types. Instead, the declaration transports a runtime type specification for its arguments. The parser uses that runtime specification to validate calls and then creates a generic invocation wrapper.

This preserves type safety without pretending that every user-defined function already exists as a static GM entity type.

## Clauses

Some block instructions can accept named clause blocks. `Switch` is the current example:

```hiconic-template
%(switch session.kind)
	%(when (eq session.kind SessionKind::keynote))
		<p>Opening keynote</p>
	%(default)
		<p>Track session</p>
	%(end)
```

Clause marker names correspond to concrete modeled clause types such as `When`, `Case` and `Default`, not to the property name on the owner. The owner property may be called `cases`; its reflected type controls which clause entity types the parser accepts.

`Switch` is clause-only: it does not accept an implicit primary content block except structural whitespace.

## Safe output

`OutputNode` emits text from `SafeOutput`. Raw objects are not blindly appended. Instead, expression results are transformed through modeled transformer descriptors such as `HtmlEsc`, `XmlEscape`, `JsonEscape`, `FormatDate`, `FormatNumber` or explicit `NoEscape`.

For example:

```hiconic-template
${input.publishedAt | (format-date "yyyy-MM-dd")}
```

The current explicit pipeline syntax uses modeled VD entity syntax for transformer stages. A more compact top-level pipe form remains an open authoring improvement.

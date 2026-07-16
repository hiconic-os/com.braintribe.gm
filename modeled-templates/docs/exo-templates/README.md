# Hiconic Models and Modeled Templates

The hiconic model layer forms an exo-type-system: a reduced, normalized, reflected and reactive type system for transportable model data that can ideally leave a single process space and still remain structurally meaningful.

Modeled templates are the demonstration case used here. They show what becomes possible when a domain-agnostic algorithm is allowed to operate on reflected and reactive models instead of strings, maps and ad-hoc runtime conventions.

This is not a rendered vision document. The template engine described here is real code in this repository. It parses template source, builds modeled template trees, resolves reflected property paths, completes value descriptors and transformer chains, validates types before runtime data is available, evaluates templates, and is covered by unit tests. The conference examples in this documentation are backed by actual templates, a real GM test model, deterministic generated data and JUnit assertions.

## The layers

1. Hiconic models define an exo-type-system.
2. Reflection exposes those models as `GenericModelType`, `EntityType`, `Property`, enum, collection and metadata structures.
3. Reactivity is embodied by PropertyAccessInterceptor facilities and value-descriptor-backed property values, enabling lazy loading, manipulation tracking, evaluable structures and canonical MVC-style signal coupling.
4. Domain-agnostic algorithms can use that reflected and reactive structure for parsing, validation, routing, conversion, evaluation and tooling.
5. Modeled templates are one such algorithmic application: templates become typed model trees, not loose macro text.
6. A dedicated hiconic PL would make this far more natural than Java can, because the model operations would be syntactically native instead of mediated through Java interfaces, reflection helpers and generated entity accessors.

## Why templates are a strong showcase

Template engines usually sit at a painful boundary between systems: domain data enters, text leaves, and mistakes become visible late. That boundary is exactly where an exo-type-system is valuable.

With hiconic models, a template parser can validate eagerly without sample data:

- Does `input.venue.city` exist?
- Is `session.startsAt` a date?
- Can `format-date` accept that value?
- Does a declared instruction argument match its runtime type specification?
- Does a collection loop introduce a correctly typed variable?
- Does an output expression end in a `SafeOutput`?

That is the leap beyond typical template engines: the model gives the engine a canonical transition layer between system parts. It supports separation of concerns, inversion of control and generic tooling because structure is explicit, reflected and reactive.

## What is real in this repository

The examples are not illustrative pseudocode or aspirational mockups. They are backed by:

- the demo GM model in `modeled-templates-test-model/src/dev/hiconic/template/test/model/demo`;
- deterministic demo data in `modeled-templates-test/src/dev/hiconic/template/docs/ConferenceDemoData.java`;
- template resources in `modeled-templates-test/res/docs`;
- JUnit coverage in `modeled-templates-test/src/dev/hiconic/template/docs/ModeledTemplateDocumentationExamplesTest.java`.

## Reading path

- [Exo-Type-System Principles](exo-type-system-principles.md)
- [Template Architecture](template-architecture.md)
- [Language Inventory](language-inventory.md)
- [Examples - Conference Domain](examples/conference-domain.md)
- [Examples - Conference Agenda](examples/conference-agenda.md)
- [Examples - Speaker Catalog](examples/speaker-catalog.md)
- [Examples - Ticket Text](examples/ticket-text.md)
- [Open Ends](open-ends.md)

## The central claim

Hiconic models can act as an exo-type-system: visible outside the host language, transportable across boundaries, reduced enough to be canonical, reflected enough for generic algorithms, reactive enough for evaluable structures and precise enough for eager validation.

Modeled templates make that claim concrete. They show how a normally stringly typed problem becomes a model-driven pipeline: parse into modeled AST, complete with experts, validate against reflected types, evaluate with explicit value descriptors and emit safe output.

The larger implication is the PL. Java proves the concept but also exposes the friction. A dedicated hiconic programming language could make these model operations direct, compact and memory-efficient, while preserving the same structural clarity.

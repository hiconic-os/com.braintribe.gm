# Exo-Type-System Principles

The exo-type-system is the hiconic model layer itself.

It is "exo" because it is not confined to the host programming language or a single process-local implementation. A modeled entity can ideally be transported, persisted, inspected, cloned, enriched and evaluated outside the immediate code that created it. Its structure remains meaningful because the model travels with a reduced, normalized, reflected and reactive type space.

Modeled templates are only one application of this idea. They are useful here because templates stress exactly the boundaries where weak systems fail: data enters from one part of a system, text leaves toward another part, and many errors would normally appear only at runtime with concrete data.

## The four qualities

Hiconic models matter because four qualities meet in one structural substrate.

### Reduced

The hiconic model type system is intentionally much simpler than the full endo-type-system of modern general-purpose programming languages. It has entities, properties, enums, base values, collections, maps and metadata. That reduction is not a weakness. It is what makes the structure stable enough to be transported, reflected and understood by domain-agnostic algorithms.

### Normalized

The model layer reduces accidental variation. A property is a property. An enum constant is an enum constant. A collection has an element type. A map has key and value types. This gives system boundaries a canonical structural language instead of a pile of local conventions.

### Reflected

Models are reflected internally by the runtime and externally by their own modeling capacity. `GenericModelType`, `EntityType`, `Property`, `GmMetaModel`, metadata and model oracles expose enough structure for algorithms such as templates, ORM, marshalling, validation, UI generation and routing to operate without being domain-specific.

### Reactive

Models are not merely passive data records. Reactivity is embodied by the PropertyAccessInterceptor facility. PAI is used for lazy loading, manipulation tracking and, in this template case, evaluator-aware access to values.

In conjunction with the ability to place `ValueDescriptor` instances into property values without corrupting strict data typing on the actual entity model, seemingly plain data entities can become evaluable structures. A property may look like ordinary domain data from the outside while internally being backed by a descriptor that can be evaluated in context.

The same reactive quality is visible in the hiconic explorer, where ManipulationTracking is used to implement MVC canonically throughout data displays with solid signal coupling. The term "reactive" is therefore not metaphorical here; it names an actual model capability.

## Structural model data

Hiconic models make structure explicit:

- `GenericModelType` describes values, entities, enums, collections and maps.
- `EntityType<T>` reflects modeled entity shapes and their `Property` definitions.
- `GmMetaModel` and `CmdResolver` make model spaces visible through an oracle and metadata resolver.
- `ValueDescriptor` represents evaluable values as modeled entities, not as opaque callbacks.
- PropertyAccessInterceptor facilities let access become lazy, tracked or evaluator-aware without changing the modeled entity surface.
- `Reason` and `Maybe` carry failure as modeled information.

The point is not just type safety in the Java sense. The point is that structure becomes available to domain-agnostic algorithms.

## Canonical transitions between system parts

Software systems constantly cross boundaries:

- persistence to runtime;
- service to client;
- model to view;
- parser to evaluator;
- configuration to behavior;
- generated artifacts to handwritten extension;
- one process space to another.

Without a canonical structural layer, every boundary accumulates local conventions: maps, strings, helper registries, undocumented object shapes, reflection hacks and late exceptions.

The hiconic model layer can canonicalize these transitions. It gives both sides a shared structural language without forcing all behavior into the same code module. That supports separation of concerns, inversion of control and generic algorithms.

## Reflection and reactivity as algorithmic fuel

Reflection makes algorithms smarter without making them domain-specific. Reactivity makes those algorithms live in the presence of lazy, tracked or evaluable values.

A template parser does not need to know a `Conference` class by hand. It asks the model:

- What is the root type?
- Does this property exist?
- What is the type of this path?
- Is this enum literal valid?
- Is this collection element assignable?
- Which metadata constrains this property?

The same principle applies outside templates. Any algorithm that can operate over reflected entities, properties, collections, metadata, value descriptors and PAI-mediated access can become reusable across domains.

## Reactive and evaluable models

The model layer is not limited to passive data. Values can be direct, or they can be backed by `ValueDescriptor` entities. Property access can respect that internal value-descriptor situation through the same conceptual access interception family used for lazy loading and manipulation tracking. Collections can contain descriptors. Evaluation can therefore stay modeled instead of disappearing into arbitrary Java code.

This is where PAI-style thinking matters: access, evaluation and propagation can remain explicit in the model graph. The machine and the programmer can talk about the same thing.

## Why eager validation matters

The template showcase demonstrates eager validation without concrete runtime data.

Given only the reflected root type, the parser can know that:

- `input.venue.city` is a valid path;
- `session.startsAt` is a date;
- `(format-date "yyyy-MM-dd")` can transform a date to string;
- string output in an HTML factory must become safe HTML output;
- `Track.children` is a collection of `Track`;
- a recursive declared instruction call receives a `Track` and an integer depth.

Given reactive access and value-descriptor-backed values, the evaluator can later run through apparently plain entity graphs while still honoring computed values, substitutions and context-sensitive evaluation.

This is a major automation advantage. AI-generated or human-written programs can be checked against structural truth earlier. The system can reject invalid transitions before production data exists, and still evaluate reactive structures when data becomes available.

## Why a dedicated PL matters

Java can host the proof, but it is not the natural home of the idea.

In Java, modeled access appears as generated interfaces, property literals, reflection helpers, value descriptor entities, PAI hooks and registry bindings. That works, but it is verbose and indirect. A dedicated hiconic PL could make modeled access, structural typing, value descriptors, property references and reactive evaluation syntactically native.

The target is not just prettier syntax. It is less accidental machinery, more direct model semantics, and a programming environment where reflective and reactive models are the natural substrate rather than a library fighting the host language.

# Reasoned value descriptor processing

This artifact provides an additive, reasoned evaluation and materialization path for value descriptors. It does not
replace or alter the established value descriptor evaluator.

The evaluator and the materializer deliberately have separate responsibilities:

- An expert evaluates one semantic descriptor type and returns a `Maybe`.
- Typed expert getters see nested descriptors through a dynamically scoped property access interceptor.
- An unsatisfied nested value crosses a typed getter through `UnsatisfiedMaybeTunneling`; the evaluator boundary
  restores the original `Maybe`. `ReasonException` is not used as a tunnel.
- Expert results are borrowed. The materializer feeds them back through normal GM cloning, so original graph fragments
  never leak into the assembled result and nested descriptors are still processed.
- A residual policy may preserve a descriptor graph for explicitly accepted reasons. Preserving is distinct from a
  successful evaluation and from an error.

`AbstractDirectCloning` only gained an overridable actual-type hook for collection members. Its default implementation
is the historic one, so existing cloners retain their behavior.

Modeled configuration currently exposes the new evaluator as an explicit reasoned path. The established strict and
partial APIs remain unchanged until their parity and partial-resolution requirements have been covered completely.

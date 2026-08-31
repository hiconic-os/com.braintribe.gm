# Value descriptor expression processing

Parses and renders compact, nested value-descriptor expressions. Function semantics are learned from supplied
`ValueDescriptor` model types: their short type names define function names and `@PositionalArguments` defines
argument binding. The parser itself contains no knowledge of concrete functions.

This artifact is independent of YAML. Marshallers may use it as an expression codec while evaluation remains the
responsibility of value-descriptor processing.

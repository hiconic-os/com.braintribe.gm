# Modeled Templates — offene Punkte

Diese Datei enthält nur echte Restarbeiten. Implementierte Festlegungen stehen im Handover und in der Tiefenreferenz.

## Priorität 0: Arbeitsstand transportieren

Der aktuelle große Umbau muss als bewusst geprüfter Commit auf dem Remote liegen, bevor auf dem Desktop neu ausgecheckt wird. Untracked Model-, Parser-, Evaluator-, Test- und VS-Code-Dateien sind besonders kritisch.

Definition of done:

- `git status` enthält nach dem Commit keine versehentlich ungesicherten Quelldateien.
- Commit ist auf dem gewünschten Remote/Branch sichtbar.
- Frischer Checkout kann die unten genannten `hc`-Kommandos ausführen.

## Priorität 1: Serialisierung und Transport des Templategraphs

Die Architektur behauptet zu Recht, dass der AST ein transportierbarer GM-Graph ist; der Nachweis als Integrationsfeature fehlt noch.

Zu testen:

- parsen, serialisieren, deserialisieren, ohne Source evaluieren;
- declared instructions, RuntimePropertySpecifications, Symbols und VariableDefinitions;
- TypeReferences und PlainCollections;
- PropertyReference-Caches bleiben transient und werden nach dem Laden wieder aufgebaut;
- Experten werden nach dem Laden deterministisch gebunden;
- SourceRanges/Reasons bleiben sinnvoll oder werden bewusst optional transportiert.

## Priorität 2: Extension-Metadata vollständig normalisieren

Bereits vorhanden sind PositionalArguments, Type-Alias-Auflösung, ScalarEntityParser und Expertenregistratur. Noch nicht vollständig generisch beschrieben/umgesetzt:

- Property-Aliase über `Alias`-MD im reflected Binder;
- Metadata für primären und sekundäre Blocks statt verbleibender Konventionen;
- deklarative Scope-/VariableDefinition-Zuordnung für ungewöhnliche Blockformen;
- explizite Variadic-/Remainder-Hinweise, Required/Optional, Dokumentation und Deprecation als Parser-Metadata;
- Kollisionsdiagnosen für Type-/Property-Aliase mit Kandidatenliste.

Ziel: Eine externe modellierte Directive soll ohne Built-in-Parserzweig dieselbe Syntax-, Completion- und Diagnosequalität erhalten.

## Priorität 3: Diagnosemodell abrunden

Parse- und PropertyPath-Diagnosen sind bereits positionsgenau. Noch offen:

- Collection-Mutationsfehler zur Runtime mit Instruction-/Property-Range und Modellpfad;
- strukturierte expected/actual types statt nur Text;
- sichtbare Variablen und Typkandidaten bei Lookup-Fehlern;
- Vorschläge bei unbekannten Properties, Aliases und Enum-Konstanten;
- aggregierte Runtime-Reasons bei verschachtelten Auswertungen.

## Priorität 4: Security- und Mutationspolicy

Offene Policy-Fragen:

- `allowNoEscape` granular nach Output-Kontext;
- erlaubte Custom-VDs/Konversionen/Directives;
- darf ein Template referenzierte Input-Entities oder nur lokale Variablen mutieren;
- Ressourcen-/Iterations-/Rekursionstiefenlimits;
- Verhalten bei nicht modifizierbaren Java-Collections.

Die aktuelle Readonly-Regel schützt Binding-Wurzeln, ist aber bewusst nicht deep-readonly.

## Priorität 5: Sprachumfang nach realem Bedarf

Nicht entschieden beziehungsweise nicht implementiert:

- condition-basierte Schleife (`while`/`for`) und `repeat n`;
- gezielte Listenentfernung per Index (`remove-at`) gegenüber dem vorhandenen wertbasierten `remove`;
- `break`/`continue` und deren Block-/Reason-Semantik;
- Quote/Escape eines beliebigen Values/VDs als Datenwert;
- weitere Operator-Zuckersyntax. Die derzeitige VD-Entity-Syntax bleibt vollständig und sollte nicht vorschnell verdoppelt werden.

Diese Punkte sind keine Lücken der Grundarchitektur. Erst anhand konkreter Templates priorisieren.

## Priorität 6: Performance und Produktionsreife

- Default-Konversionspfade und Reflection-Lookups cachen;
- Parser-/Evaluation-Benchmarks für große Templates und tiefe Collectiongraphs;
- maximale Rekursions-/Graph-/Outputgrößen;
- Thread-Safety und Wiederverwendung eines geparsten Templates explizit testen;
- generischen transienten JVM-Cache für modellierte Defaults prüfen statt `ResolvedTemplateDefaults` weiter zu spezialisieren.

## Priorität 7: Öffentliche Dokumentation

Aus der technischen Referenz ableiten:

- kurze User Syntax Guide;
- Extension/Expert Authoring Guide;
- Security und Output Safety Guide;
- Diagnostics Guide;
- Cookbook für HTML, JSON, XML, YAML, CSS und JavaScript.

Die große Architekturdatei soll technische Wahrheit bleiben, aber nicht zugleich Tutorial, Änderungsjournal und Backlog sein.

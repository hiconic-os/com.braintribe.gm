# Modeled Templates — kompakter Handover

Stand: 2026-07-13. Dieses Dokument ist der Startkontext für einen neuen Menschen oder Agenten. Es hält Festlegungen fest, nicht die Gesprächschronologie.

## 1. Ziel und Qualitätsprinzip

Modeled Templates ist eine Templatesprache als Projektion auf ein reduziertes/reflektiertes/reaktives Exo-Typsystem:

```text
source
-> positionsbewusster Parser
-> reflektiert gebundene GM-Entities
-> Experten-Completion
-> statische Validierung
-> wiederverwendbarer modellierter Templategraph
-> typisierte Runtime-Evaluation
```

Die zentrale Regel lautet: Sprachbausteine sind Anwendungen allgemeiner Modell- und Parserfähigkeiten, keine Built-in-Sondergrammatiken. Extensions benutzen dieselben Mechanismen wie die Grundausstattung.

## 2. Nicht wieder aufrollen

Diese Entscheidungen sind verbindlich:

- Directives verwenden `%()`; Output verwendet `${}`; rohe Kommentare verwenden `#{}`.
- Sigils inferieren beziehungsweise beschränken Entity-Typen. `%()` erlaubt `DirectiveNode`, `${}` inferiert `OutputNode`; `#{}` ist der bewusst rohe Textfall.
- `{...}` konstruiert einen aus dem Binding-Ziel inferierten Entity- oder Map-Typ. `(type-name ...)` ist explizite Entity-Syntax.
- Named arguments verwenden `name: value`, nicht POSIX-Optionen und nicht `name=value`.
- Argumente und Collection-Elemente werden whitespace-separiert; Kommas sind nicht erforderlich.
- `|` ist allgemeine Value-Syntax. `left | (vd args...)` speist `left` in den nächsten freien positional Slot beziehungsweise in die laufende variadische Collection ein.
- Pipe-Stufen bleiben gewöhnliche VDs. `TransformValue` und ein eigener Transformer-Ast wurden entfernt.
- `type` und `T` sind `Alias`-Namen von `TypeReference`: `(type string)`, `(T map< string, Person >)`. Der registrierte ScalarEntityParser konsumiert den gesamten Rest und normalisiert Whitespace in Type-Signatures.
- `Symbol`, `TypeReference`, `VariableDefinition` und `AssignmentTarget` sind modellierte well-known Types mit Scalar-to-Entity-Parsern.
- `null` ist ein gewöhnliches Literal, überall assignable, aber durch Property-Nullability und `Mandatory` begrenzt. Ein interner Marker unterscheidet explizites `null` von „nicht angegeben“.
- `DynamicEntityType` wird nicht für declared instructions benutzt. Runtime-Signaturen bleiben transportierbare modellierte Daten.
- Variablen werden über `VariableDefinition` reflektiert. Experten publizieren completed Definitionen über `VariableDefiningNode.variableDefinitions`; der Parser kennt keine Built-in-Loop-Sonderregel.
- Sichtbare Variablen dürfen nicht geschattet werden; gleiche Namen in disjunkten Scopes sind erlaubt. Loop-/Parameter-/Input-Bindings sind readonly, `Var` ist mutable.
- Property-Pfade sind eigene gestaffelte Modelle mit Symbolen, PropertyReferences, ListIndexAccess und MapKeyAccess; keine undurchsichtigen Strings.
- `?./?[...]` ist ausschließlich lenienter R-Value-Zugriff. L-Values sind strikt und verbieten optionale Segmente.
- Maps laufen nicht durch `for-each`; `for-each-entry` besitzt getrennte optionale `key:`- und `value:`-Definitionen mit aus `K`/`V` inferierten readonly Typen.
- Modellierte Defaults werden bereits durch `EntityType.create()` angewendet.
- Testprojekte werden getestet, niemals installiert. Installiert werden nur tatsächlich geänderte abhängige Projekte.

## 3. Typhierarchie

Die Rollen sind orthogonal:

```text
TemplateNode
  TextNode / SequenceNode / OutputNode / ErrorNode / CommentNode
  DirectiveNode
    InstructionNode       runtime effect
    DeclarationNode       parser/symbol effect
    BlockNode             block ownership and scope
    SilentNode            no output at source position
    StatementInstructionNode = InstructionNode + SilentNode
```

Beispiele:

- `Var`: Declaration + Instruction + VariableDefining + Silent.
- `DeclareInstruction`: Declaration + Block + Silent, aber keine künstliche Runtime-Instruction.
- `ForEach`: Instruction + Block + VariableDefining.
- `Set` und Collection-Mutationen: StatementInstruction.

Whitespace ist auf `DirectiveNode.whitespace : WhitespacePolicy` modelliert. `preserve`, `trim` und `trimLine` sind implementiert. Statement-only-Zeilen werden standardmäßig sauber entfernt; Inline-Whitespace bleibt erhalten.

## 4. Syntaxkompass

```text
${input.name}
#{ raw comment }

%(if condition) ... %(else) ... %(end)
%(switch value) %(case x) ... %(default) ... %(end)
%(for-each persons person) ... %(empty) ... %(end)
%(for-each-entry byId key: id value: person) ... %(empty) ... %(end)

%(var name string value: "Ada")
%(var inferred value: input.name)
%(set name (concat "Dr. " input.name))
%(set person.address.city "Vienna")

%(append names "Ada")
%(insert names 0 "First")
%(add tags "new")
%(put people "ada" {name: "Ada"})
%(remove names "Ada")
%(remove people "ada")

%(declare-instruction greet {name string} {punctuation string default: "!"})
  Hello ${name}${punctuation}
%(end)
%(greet "Ada")
```

Declared instructions unterstützen Selbstrekursion, gegenseitige Rekursion und Forward References im selben lexikalischen Scope durch einen Predeclaration-Pass. Tests dürfen keine Tests als Dependencies enthalten.

## 5. Values, Literale und Typen

Implementiert:

- String mit Escapes, boolean, `null`, Enum `::value`.
- Integer/Double ohne Suffix; explizite Long-/Float-Formen.
- inferred `{...}`-Entities und Maps; explizite `(Type ...)`-Entities.
- `[...]`, `list<T>[...]`, `set<T>[...]`, `map<K,V>{...}` als typtragende PlainCollections.
- Keine direkte Collection-in-Collection-Schachtelung, außer die äußere Position ist bewusst `object`.
- Collection-`null` ist assignable; Duplicate Map Keys werden bei bekannten Literalen abgewiesen.
- Variadische positional Collection-Properties erhalten einzelne Argumente; ein explizites Collection-Literal bindet die Property als Ganzes.

Wichtige VDs:

```text
And Or Not
Eq Ne Gt Ge Lt Le Like
Add Subtract Multiply Divide
Concat
Cast
TypeOf DeclaredTypeOf Is AssignableTo
FormatDate FormatNumber
HtmlEsc XmlEscape JsonEscape JavaScriptEscape CssEscape
UrlComponentEscape JavaLiteralEscape NoEscape
```

`TypeOf` liefert den konkreten Runtime-Typ, `DeclaredTypeOf` den completion-time bekannten Typ. `Eq`/`Ne` vergleichen TypeReferences über Signatures, inferieren aber bewusst nicht einen `object`-Operanden aus dem anderen; dort `(type ...)` benutzen.

## 6. Completion und Validierung

Reihenfolge:

```text
tokenize with offsets
-> reflected argument binding
-> nested VD completion
-> owner expert completion
-> scope publication
-> block parsing
-> graph completion/validation
-> CMD metadata constraints
```

Der Binder prüft unbekannte/doppelte Properties, positional-after-named, Anzahl, Variadics, Nullability und Assignability. Experten dürfen über `expectedArgumentType` den erwarteten Typ aus bereits gebundenen Properties ableiten, etwa `Var.value`, `Set.value`, Collection-Mutationswerte oder VariableDefinition.default.

CMD-basierte Direktwert-Constraints sind implementiert:

```text
Mandatory Pattern Min Max MinLength MaxLength
```

VD-backed Werte werden nicht gegen erst zur Runtime bekannte wertabhängige Constraints geraten. Der Descriptorgraph selbst wird vollständig typisiert und validiert.

Fehler tragen Source-Ranges. Binder-/Completion-/Constraint-Fehler besitzen genaue Argumentbereiche und Modellpfade; PropertyPath-Runtime-Reasons enthalten Pfad, Segment, Operation und Access-Range.

## 7. Evaluation und Output-Sicherheit

- VDs werden über registrierte `VdEvaluator`s ausgewertet.
- `EvaluationPai` macht VD-backed Modellproperties für Experten transparent.
- PlainList/PlainSet/PlainMap bewahren parametrisierte Typinformation.
- Collection-Werte werden rekursiv materialisiert und nur bei tatsächlichen VD-Änderungen kopiert.
- Output-Sicherheit ist typisiert (`HtmlOutput`, `JsonOutput`, ...), nicht nur Konvention.
- Factory-Profile registrieren Default-Konversionen; `NoEscape` bleibt explizit und policy-gesteuert.
- Locale, Zone und Formatierung haben modellierte Defaults und explizite Overrides.

## 8. Erweiterungsmodell

Eine Extension liefert modellierte Typen plus Experten:

- neuer Wert: `ValueDescriptor` + `VdEvaluator`;
- neue Konversion: gewöhnlicher VD + `ValueConversion`;
- neue Directive: konkreter `DirectiveNode` mit passenden Rollen + `TemplateNodeEvaluator`;
- neue skalare Entity-Kurzform: `ScalarEntityParser`;
- alternative Namen: `Alias`-MD;
- positional binding: `PositionalArguments`;
- Scope-Definitionen: im Experten completen und über `variableDefinitions` publizieren.

Der Parser soll keine Extension-Klasse namentlich kennen.

## 9. Implementierter Stand

- Parser mit Reader/InputStream/File/Path/String, Block-Wiring und Recovery `STRICT`, `SUBSTITUTE`, `SILENCE`.
- Declared instructions inklusive Defaults, Runtime Arguments, Rekursion und Forward References.
- Variablen, AssignmentTargets, strikte/optionale Property-/List-/Map-Pfade.
- If, Switch, ForEach, ForEachEntry, Set und Collection-Mutationen.
- Literale, TypeReferences, allgemeine Pipes, VDs, Konversionen und Output-Safety.
- Metadata-Constraints und genaue Parse-/Runtime-Lokalisation.
- VS-Code-TextMate-Grammatik für `.mt` und eingebettete HTML/JSON/YAML/XML/Markdown/CSS/JS/TS-Varianten.
- Zuletzt: `modeled-templates-model hc install`, `modeled-templates hc install`, `modeled-templates-test hc compile && hc test`; 77 Tests erfolgreich.

## 10. Wiederaufnahme

Erster Auftrag an einen neuen Agenten:

```text
Lies modeled-templates/README.md, modeled-template-handover.md,
hiconic-template-system.md und modeled-template-open-issues.md vollständig.
Prüfe danach git status und die letzten Tests. Behandle Code und Tests als
ausführbare Wahrheit. Ändere keine festgelegte Syntax ohne explizite Diskussion.
Nutze hc compile/install/test; Testprojekte nie installieren.
```

Vor dem Rechnerwechsel:

```text
git status
git diff --check
git add <bewusst ausgewählte Dateien>
git commit
git push
```

Ein lokales `hc install` ist kein Ersatz für Commit und Push.

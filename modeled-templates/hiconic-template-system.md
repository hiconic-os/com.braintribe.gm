# Hiconic Template System — konsolidierte Architektur- und Agentenreferenz

Stand: 2026-07-11

Dieses Dokument ist als dichter Wiedereinstieg gedacht: ein Mensch oder ein Codex-Agent soll damit auf einer anderen Maschine verstehen, was hier gebaut wurde, welche Architekturentscheidungen gelten, welche Syntax der Parser versteht, wie GM/hiconic-Modelle, ValueDescriptoren, Experten, PAI und declared instructions zusammenspielen und welche offenen Enden als nächstes wichtig sind.

## 1. Kurzdefinition

Ein hiconic Template ist kein String mit Helferfunktionen, sondern ein reflektierter GM-Modellgraph aus `TemplateNode`-Entitäten. Normale, typisierte Modell-Properties können durch `ValueDescriptor`-Backing dynamisch sein. Während der Evaluation sorgt eine `PropertyAccessInterceptor`-Schicht dafür, dass Getter wie `ifNode.getCondition()` oder `concat.getOperands()` bereits den evaluierten Wert liefern. Parser, Completion, Validierung und typisierte VD-Konversionen stellen vorher sicher, dass diese Werte zu den deklarierten GM-Typen passen — besonders zu `SafeOutput` bei `${...}`.

Der entscheidende Paradigmenwechsel:

```text
Template source
-> parser
-> reflected TemplateNode graph
-> expert completion + validation
-> evaluated through TemplateEvaluationContext + PAI
-> text output
```

Es gibt keine untypisierte Helper-Map, keinen zufälligen Entity-`toString()`-Fallback und keine blinde Raw-Ausgabe. Erweiterung passiert über Modelle und Experten.

## 2. Artefakte und Verantwortlichkeiten

Wichtige Artefakte:

- `modeled-templates-model`: GM-Modell des Template-Systems.
- `modeled-templates`: Parser, Experten, Evaluatoren, API/Builder, Runtime-Kontext.
- `modeled-templates-test-model`: Testmodell mit Input-/Literal-/Parser-Testtypen.
- `modeled-templates-test`: JUnit-Tests für Parser, Resolver, Runtime, Factory-API.

Build-Hinweis:

```text
hc compile   // lokales Artifact kompilieren
hc install   // lokales Artifact installieren, wichtig für abhängige Artifacts
hc test      // unit-test Artifact ausführen
```

Model/core Artifacts bleiben Java-8-kompatibel, Parser/Runtime-Artefakte dürfen moderner sein.

## 3. Zentrale Modellschichten

### 3.1 Template-Knoten

Das Template wird als `TemplateNode`-Graph modelliert.

Aktuelle wichtige Typen:

```text
TemplateNode
  TextNode
  SequenceNode
  CommentNode
  ErrorNode
  OutputNode
  ArgumentedNode
  instr.DirectiveNode
    instr.InstructionNode
    decl.DeclarationNode
  instr.BlockNode
    instr.BlockInstructionNode
      instr.If
      instr.ForEach
      instr.Switch
    decl.DeclareInstruction
  instr.SilentNode
    instr.StatementInstructionNode
    decl.DeclareInstruction
    CommentNode
```

Die Hierarchie beschreibt orthogonale Rollen. `DirectiveNode` ist die Typschranke der `%()`-Syntax und trägt die gemeinsame Whitespace-Policy. `InstructionNode` bezeichnet Runtime-Ausführung, `DeclarationNode` statische Wirkung auf Parser und Symbolräume, `BlockNode` Block und Blockbindings und `SilentNode` fehlenden Output an der syntaktischen Stelle. `Var` ist Declaration, Runtime-Statement und SilentNode zugleich; `DeclareInstruction` ist Declaration, BlockNode und SilentNode, aber keine künstliche Runtime-Instruction.

Wichtige Entscheidung: Ein Block ist kein eigener `BlockReference`, sondern wieder ein `TemplateNode`. Wenn ein Block genau einen Knoten enthält, wird dieser Knoten direkt verwendet. `SequenceNode` wird nur für mehrere Knoten benötigt. Das gilt auch auf Root-Ebene.

### 3.2 Output-Sicherheit

`OutputNode` ist bewusst so modelliert:

```text
OutputNode.output : SafeOutput
```

Damit ist Sicherheit eine Typeigenschaft des Modellgraphen, keine Konvention im Renderer.

`SafeOutput` hat bewusst mehrere Derivate:

```text
HtmlOutput
XmlOutput
JsonOutput
JavaScriptOutput
CssOutput
UrlComponentOutput
JavaLiteralOutput
RawOutput
```

Diese Typen tragen oft keine zusätzlichen Properties. Sie sind Phantom-/Marker-Typen im GM-Sinn: sie kodieren den Output-Kontext im Typsystem statt in einem losen String-Feld.

### 3.3 Wertkonversionen als ValueDescriptoren

Es gibt keine separate Transformer-Hierarchie. Formatierung, Escaping, Casting und weitere Wertoperationen sind normale `ValueDescriptor`-Entitäten:

Wichtige Typen:

```text
HtmlEsc
XmlEscape
JsonEscape
JavaScriptEscape
CssEscape
UrlComponentEscape
JavaLiteralEscape
NoEscape
FormatDate
FormatNumber
Cast
```

Sie werden durch `VdEvaluator` beziehungsweise registrierte `ValueConversion`-Experten ausgewertet. Registrierte Default-Konversionskanten dienen ausschließlich der Completion eines erwarteten Zieltyps; explizite und automatisch ergänzte Konversionen erzeugen denselben VD-Modellgraphen:

```text
Date   -> FormatDate -> String
Number -> FormatNumber -> String
String -> HtmlEsc    -> HtmlOutput <: SafeOutput
```

### 3.4 ValueDescriptoren

Die funktionale Ausdrucksebene wird nicht als zweiter AST erfunden, sondern durch GM-`ValueDescriptor`-Typen modelliert.

Aktuelle Template-VD-Typen:

```text
Boolean:
  And, Or, Not

Vergleich:
  Eq, Ne, Gt, Ge, Lt, Le, Like

Arithmetik:
  Add, Subtract, Multiply, Divide

String/variadisch:
  Concat

Konversion und Typbetrachtung:
  FormatDate, FormatNumber, HtmlEsc, ...
  Cast
  TypeOf, DeclaredTypeOf, Is, AssignableTo
```

`TransformValue` existiert nicht. Die Pipe-Syntax normalisiert direkt zu gewöhnlicher VD-Schachtelung.

Die Evaluatoren implementieren `VdEvaluator<V, O>`.

Arithmetik ist absichtlich über `Object`-Properties modelliert, weil `Number` keine GM-Abstraktion ist. Type-Checks laufen über `ValidationContext.getType(entity, property)` und beachten ValueDescriptor-Backing.

Typen sind selbst modellierte Werte vom Typ `TypeReference`:

```text
(type-of value)                    // konkreter reflektierter Runtime-Typ
(declared-type-of value)           // completion-time bekannter Expression-Typ
(is value Person)                  // target: TypeReference, daher Person inferiert
(assignable-to (type-of value) object)
(eq (type-of value) (type string)) // explizit an einer offenen object-Position
(eq (type-of value) (T string))    // kompakter Alias derselben Entity
```

`(type xyz)` ist keine eigene Literalgrammatik, sondern gewöhnliche explizite Entity-Syntax: `type` ist der lesbare, `T` der kompakte `Alias` des modellierten `TypeReference`-Typs. Weil für `TypeReference` ein `ScalarEntityParser` registriert ist, erhält dieser den gesamten unveränderten Rest nach dem Typnamen. Dadurch sind auch `(type list< string >)` und `(T map< string, Person >)` gültig; die inneren Whitespaces werden nicht als mehrere Entity-Argumente tokenisiert. Eine explizit benannte Property wie `(type typeSignature: string)` schaltet weiterhin auf den gewöhnlichen strukturellen Binder. Die skalare Form funktioniert insbesondere an einer offenen `object`-Position und bleibt für Extensions über dieselbe Alias- und Scalar-Parser-Logik verfügbar. Unbekannte Typen werden beim Parsen abgewiesen.

`type-of null` liefert eine kontrollierte Runtime-Reason, weil `null` keinen konkreten Runtime-Typ besitzt; `is null (type string)` ist dagegen `false`. Typtragende PlainCollections liefern ihre vollständige Parametrisierung. `Eq` und `Ne` vergleichen zwei `TypeReference`-Werte strukturell über ihre Type-Signature statt über Entity-Identität. Sie inferieren aber bewusst keinen Operanden aus dem jeweils anderen: Beide Properties bleiben semantisch `object`, weshalb dort `(type ...)` beziehungsweise `(T ...)` explizit verwendet wird.

Verschachtelte dynamisch typisierte VDs werden unmittelbar nach ihrer Argumentnormalisierung durch ihren Experten completed, bevor der umgebende Binder nach `valueType()` fragt. Der spätere Graph-Pass wiederholt Completion und Gesamtvalidierung idempotent. Damit steht completion-driven Typwissen in jeder Schachtelungstiefe rechtzeitig zur Verfügung und nicht nur innerhalb einer Pipeline.

### 3.5 Declared instructions und Runtime-Typspezifikation

Template-lokale Instruktionen werden nicht über `DynamicEntityType` modelliert. Das wurde verworfen, weil `DynamicEntityType` nicht sauber als transportierbare, reflektierte Subtypstruktur mit Supertypes funktioniert und außerdem Modell-/Transportsemantik verwässert.

Stattdessen gibt es eine modellierte Runtime-Signatur:

```text
RuntimeTypeSpecification
  name
  properties : List<RuntimePropertySpecification>

RuntimePropertySpecification
  name
  typeSignature
  positionalIndex
  required
  metaData

RuntimeArguments
  typeSpecification
  values : List<RuntimePropertyValue>

RuntimePropertyValue
  specification
  value
```

Ein Aufruf einer declared instruction wird in einen normalen modellierten Wrapper übersetzt:

```text
InvokeInstruction
  name
  declaration : DeclareInstruction
  arguments   : RuntimeArguments
  body        : TemplateNode
```

`InvokeInstruction` ist der generische Runtime-Hebel. Framework-/Usage-seitig definierte Instructions bleiben dagegen echte konkrete `InstructionNode`-Entitäten und werden direkt typisiert instanziiert. Nur declared instructions gehen über `InvokeInstruction` und `RuntimeArguments`.

Die Evaluation nimmt den Body aus der referenzierten `DeclareInstruction`. Damit darf ein während der Vorregistrierung erzeugter Aufruf auf eine Declaration zeigen, deren Body erst anschließend geparst wird; das ist insbesondere für Forward References und gegenseitige Rekursion nötig.

## 4. Syntax

### 4.1 Hauptformen

```text
%(Type ...) expliziter DirectiveNode; `%` liefert den erwarteten abstrakten Typ
${...}     inferierter OutputNode; `$` liefert den konkreten erwarteten Typ
#{...}     inferierter CommentNode mit un-tokenisiertem SourceText
(Type ...) explizit typisierte Entity/ValueDescriptor-Form
{...}     Entity oder Map mit aus dem Binding-Ziel inferiertem Typ
|          allgemeine VD-Applikation durch Einspeisung eines positional Arguments
```

Sigils eröffnen keinen fachlichen Spezialparser, sondern liefern dem gemeinsamen Entity-Parser lediglich einen erwarteten Typ. Weil `DirectiveNode` abstrakt ist, muss nach `%` die explizite `(...)`-Form mit einem konkreten Subtyp folgen. `OutputNode` und `CommentNode` sind konkret und können deshalb durch die inferierte `{...}`-Form aufgenommen werden. Die Typschranke verhindert beispielsweise `%(gt 1 0)`, weil `Gt` ein VD und kein `DirectiveNode` ist.

Beispiele:

```text
Hello ${input.name}

%(if input.active)
  ${input.name}
%(else)
  inactive
%(end)
```

```text
%(declare-instruction greet {name string})
<b>${name}</b>
%(end)

<h1>%(greet "Dirk")</h1>
```

Das zweite Beispiel wird so interpretiert:

1. `%(declare-instruction ...)` erzeugt über denselben reflektierten Binder einen `DeclareInstruction`.
2. Während des Blocks ist `name : string` im Validierungsscope sichtbar.
3. Vor dem Parsen eines lexikalischen Blocks werden alle darin enthaltenen Deklarationssignaturen registriert. Dadurch sind Selbstrekursion, gegenseitige Rekursion und Forward References innerhalb desselben Scopes möglich.
4. `%(greet "Dirk")` wird zu `InvokeInstruction`.
5. Der generic Invocation-Evaluator projiziert `RuntimeArguments` in einen temporären Runtime-Scope und evaluiert den Body.

Variablen werden ebenfalls im Declaration-Namensraum angelegt, haben aber zusätzlich eine Runtime-Wirkung:

```text
%(var greeting string value: "Hello")
${greeting}
%(var inferred value: input.name)
```

`Var` erweitert sowohl `DeclarationNode` als auch `InstructionNode`. Für alle Variablendeklarationen ist der gemeinsame positionale Präfix `symbol type`; der Initialwert ist bewusst named:

```text
Var
  symbol: Symbol                 // positional 1
  type: TypeReference            // positional 2, optional bei value
  value: object
```

Der Completion-Experte erzeugt daraus eine `VariableDefinition` und publiziert sie über das gemeinsame `VariableDefiningNode.variableDefinitions`-Feature. Der Parser registriert diese Reflection ab der Deklarationsstelle im aktuellen lexikalischen Validierungsscope; der `VarEvaluator` bindet zur Laufzeit dasselbe kanonische Symbol. Bei explizitem Typ ist der Initialwert optional. Fehlt der Typ, ist `value:` erforderlich und dessen statischer Expression-Typ vervollständigt die Definition. Nicht eindeutige Werte wie unqualifiziertes `[]` oder `{}` benötigen deshalb einen Typ oder Decorator.

```text
%(var empty string)
%(var greeting string value: "Hello")
%(var inferred value: input.name)
```

Ein unmarkierter zweiter Wert ist nicht erlaubt: Position 2 bedeutet bei `Var` ebenso wie bei `VariableDefinition` stets `type`. Diese gemeinsame Präfixregel verhindert, dass dieselbe Tokenposition je nach Deklarationsart einmal Typ und einmal Wert bedeutet.

Variablen werden im Gegensatz zu declared instructions nicht vorregistriert. Forward References sind deshalb ungültig. Es gibt keinen zweiten syntaktischen Declaration-Namensraum: Die Rollen `DeclarationNode` und `InstructionNode` werden durch die Typhierarchie ausgedrückt, während alle modellierten Directives dieselbe `%()`-Form verwenden.

Eine sichtbare Variable wird mit der normalen, blockfreien `set`-Instruction verändert:

```text
%(var greeting string value: "Hello")
%(set greeting (concat "Hello " input.name))
%(set person.address.city "Vienna")
${greeting}
```

Der Parser behandelt das erste `set`-Argument als modelliertes `AssignmentTarget`: entweder `VariableAssignmentTarget` oder `PropertyAssignmentTarget`. Letzteres enthält einen vollständig gestaffelten `TemplatePropertyPath`, keinen String. Die rechte Seite ist eine vollständige Expression und kann Literal, Variable, Property-Pfad, ValueDescriptor, Entity oder Collection sein. Sie wird gegen den statisch aufgelösten Zieltyp geprüft. Ein Property-L-Value erzeugt keine fehlenden Zwischen-Entities; ein zur Laufzeit nicht aufgehender Pfad liefert eine modellierte `PathEvaluationError`-Reason.

Collection-Mutationen sind ebenfalls gewöhnliche, stille Statements und verwenden denselben typisierten `AssignmentTarget`:

```text
%(append names "Ada")             // list<T>, T
%(insert names 0 "First")         // list<T>, integer, T; Index 0..size
%(add tags "new")                 // set<T>, T
%(put people "ada" {name: "Ada"}) // map<K,V>, K, V
%(remove names "Ada")             // list<T>/set<T>: Element
%(remove people "ada")            // map<K,V>: Key
```

Der zuständige Completion-Experte leitet Element-, Key- und Value-Typ aus der vollständig aufgelösten Zielsignatur ab. Damit durchlaufen Literale, Variablen, Pfade und ValueDescriptoren denselben Expression-Binder und dieselbe Assignability-Prüfung wie bei `set`; es gibt keine Collection-spezifische Nebengrammatik. `append` und `insert` akzeptieren ausschließlich Listen, `add` ausschließlich Sets und `put` ausschließlich Maps. `remove` wird allein durch den statischen Zieltyp eindeutig als Element- oder Key-Entfernung bestimmt.

`input`, Instruction-Parameter und Loop-Variablen sind typisierte readonly Bindings. Nur `%(var ...)` erzeugt mutable Bindings. Readonly-Wurzeln dürfen weder direkt noch über Properties beschrieben werden. Readonly ist bewusst nicht tief: Wird eine referenzierte Entity in einer lokalen Variable abgelegt, folgt deren Mutation normaler Referenzsemantik.

Shadowing ist über die gesamte sichtbare Scope-Kette verboten. Denselben Namen in disjunkten Blöcken zu deklarieren ist erlaubt, weil keiner der beiden Bindings beim Parsen des anderen sichtbar ist.

`Set`, `Var` und die Collection-Mutationen sind `StatementInstructionNode`s und damit `SilentNode`s. `DeclareInstruction` und `CommentNode` sind ebenfalls silent, obwohl sie keine Runtime-Statements sind. Ohne explizite `whitespace:`-Policy entfernen SilentNodes deshalb automatisch eine Zeile, auf der außer dem Node nur horizontaler Whitespace steht. Inline-Whitespace bleibt unverändert.

### 4.2 Allgemeine Value-Pipelines

```text
${input.title}
${input.birthday | (format-date "yyyy/MM/dd")}
${input.birthday | (format-date "yyyy/MM/dd") | (url-component-escape)}
${input.title | (no-escape)}
```

`|` ist eine allgemeine Expression und nicht an Output gebunden. Formal beginnt `left | (Vd args...)` die positional Argumentfolge des konkreten VD mit `left` und setzt sie anschließend mit `args...` fort:

```text
a | (add 1) | (multiply 2)
== (multiply (add a 1) 2)

"a" | (concat "b" "c")
== (concat "a" "b" "c")

value | (cast Person)
== (cast value Person)
```

Der Pipe-Operator kennt weder ein `input`-Property noch besondere pipefähige VD-Subtypen. Er ruft denselben positional Binder auf wie ausgeschriebene Argumente. Zeigt dessen Cursor auf eine skalare Property, wird sie belegt und weitergeschaltet; ist eine variadische Collection aktiv, wird das nächste Element angehängt. Die Klammer rechts von `|` ist erforderlich, weil der erwartete Typ `ValueDescriptor` abstrakt ist und sie zugleich die Stage eindeutig begrenzt.

Der Binder bewahrt zusätzlich den konkreten statischen Typ jedes Arguments auf Validation-Ebene. Das ist besonders für polymorphe Modellproperties vom Typ `object` wichtig: Bei `1 | (add 2)` bleiben beide Operanden für den `Add`-Experten als `integer` bekannt, obwohl `ArithmeticOperation.left/right` modellseitig `object` sind. Extensions erhalten dieselbe Typinformation über `ValidationContext.getType(entity, property)`; weder Pipe noch Built-in-VD benötigen dafür Spezialwissen.

Fehlende Output-Konversionen werden über registrierte Default-VD-Konversionskanten ergänzt, wenn es einen eindeutigen Pfad gibt.

HTML-Profil-Beispiel:

```text
input.birthday : Date

${input.birthday}
=> Date -> FormatDate(defaults) -> String -> HtmlEsc -> HtmlOutput

${input.birthday | (url-component-escape)}
=> Date -> FormatDate(defaults) -> String -> UrlComponentEscape -> UrlComponentOutput
```

### 4.3 Remainder-Text

`SourceText` ist ein well-known modellierter EntityType für un-tokenisierten Resttext. Erwartet die letzte positionale Property eines EntityTypes `SourceText`, konsumiert der gemeinsame Argumentparser den verbleibenden Source-Span bis zum äußeren Konstruktabschluss als genau einen Wert. Eine Remainder-Property muss die letzte positionale Property sein.

`CommentNode.text : SourceText` nutzt dieses allgemeine Prinzip. Deshalb bedeutet `#{...}` lediglich: `#` inferiert `CommentNode`, dessen normaler Binder über die letzte Property den unveränderten Inhalt aufnimmt. Whitespace innerhalb des Kommentars wird nicht getrimmt. Derselbe Mechanismus steht Extension-Entities zur Verfügung und ist kein fest verdrahteter fachlicher Kommentarparser.

### 4.4 Function/VD-Namen

ValueDescriptoren und Instructions dürfen zusätzlich über lower-kebab-case Namen angesprochen werden:

```text
Add               -> add
ForEach           -> for-each
JavaLiteralEscape -> java-literal-escape
```

Ordinary payload Entities sollten dagegen richtig gecased und als Modelltypen adressiert werden. Das hält die Grenze klar:

```text
(concat "Hello " input.name)
(add 1 2)
(Person name: "Ada")       // normale Entity, richtig gecased
{ name: "Ada" }            // Typ wird aus dem Binding-Ziel inferiert
```

Enums werden explizit namespaced:

```text
SomeEnum::constant
::constant                 // nur wenn der Zieltyp eindeutig bekannt ist
```

Variablen haben Vorrang als Identifier. Enum-Konstanten ohne Namespace werden nicht mit Variablen vermischt.

### 4.5 Literale und Escaping

String-Literale verwenden doppelte Quotes und unterstützen die üblichen Escape-Sequenzen:

```text
"text"
"line\nbreak"
"quote: \""
"\u20ac"
```

Parser-Escaping existiert auch auf Template-Ebene, damit syntaktische Opener als Text erscheinen können.

Typisierte Literale:

```text
string:"abc"
boolean:true
integer:1
long:1
float:1.0
double:1.0
decimal:1.00
date:"2026-07-11T00:00:00Z"
```

Ungetypte Literale werden soweit möglich aus dem Binding-Ziel inferiert. Entity-, List-, Set- und Map-Literale sowie ihre explizit typdekorierten Formen sind implementiert; die kontrollierte Verschachtelungsregel ist in Abschnitt 4.5 beschrieben.

`null` ist ein gewöhnliches ungetyptes Literal. Der Parser hält es intern bis zur Bindung von „nicht angegeben“ auseinander und materialisiert es anschließend als echtes `null`. Es ist unabhängig vom statischen Zieltyp assignable und daher auch in List-, Set- und Map-Literalen erlaubt:

```text
%(var value string value: null)
list<string>[null "text"]
map<string,Person>{ "nobody": null }
${null}                              // erzeugt keine Ausgabe
```

Ob ein gebundener Property-Wert tatsächlich `null` sein darf, entscheiden reflektierte Property-Nullability und Constraint-Metadata. Insbesondere weist `Mandatory` ein explizites `null` ebenso zurück wie einen nicht angegebenen Wert. Die Java-Schreibweise eines Model-Getters allein wird nicht als zusätzliche Nullability-Regel interpretiert.

### 4.6 Positional und named arguments

Argument-Bindung nutzt reflektierte Properties und `@PositionalArguments`.

Beispiel:

```java
@PositionalArguments("operands")
interface Concat extends StringDescriptor {
    List<Object> getOperands();
}
```

Syntax:

```text
(concat "Hello " input.title "!")
```

Spezialregel: Wenn das letzte positionale Argument eine Collection ist, werden alle weiteren positional arguments bis zum nächsten named argument als Collection-Elemente interpretiert.

```text
(concat "a" "b" "c")
```

bindet also alle drei Werte in `operands`.

Named arguments:

```text
pattern: "yyyy/MM/dd"
```

Die Sprache verwendet bewusst keine Kommas. Whitespace trennt Argumente; Quotes und die Klammerformen begrenzen verschachtelte Werte. Positionale Argumente müssen vor named arguments stehen. Die früheren CLI-Formen `--name value` und `name=value` werden nicht unterstützt.

Inferierte strukturierte Literale verwenden `{...}`. Bei einem Entity-Property erzeugt der Parser den erwarteten EntityType; bei einem `map<K,V>`-Binding entsteht ein Map-Literal:

```text
configuration: { locale: "de-DE" retries: 3 }
values: { "first": 1 "second": 2 }
```

Collection-Literale verwenden keine Kommas. `[...]` wird aus dem Binding-Ziel als List oder Set inferiert; an einer `object`-Position ist der Default `list<object>`. `{...}` wird an einer Map-Position als Map inferiert und an einer `object`-Position als `map<object,object>`.

Die erzeugten Runtime-Werte sind stets typtragende `PlainList`, `PlainSet` beziehungsweise `PlainMap`. Das gilt auch für variadisch gebundene Collection-Properties; insbesondere wird ein `set<T>` nicht zwischen Parser und Runtime versehentlich zur Liste. Gewöhnliche Java-Collections verlieren ihre Parameterisierung und sind deshalb keine zulässige Grundlage für validation-time Typableitung. `ForEach` und `ForEachEntry` leiten ihre Laufvariablentypen ausschließlich aus dem completion-time bekannten Collection- beziehungsweise Map-Typ ab; eine datengetriebene Untersuchung der enthaltenen Runtime-Werte findet nicht statt.

Explizite Collection-Typen stehen ohne Whitespace direkt vor dem Literal:

```text
list<string>["a" "b"]
set<Person>[{ name: "Ada" } { name: "Alan" }]
map<string,Person>{ "ada": { name: "Ada" } }
```

Collections dürfen nicht direkt als Element-, Key- oder Value-Typ einer anderen Collection deklariert werden. Verschachtelung ist nur über eine bewusst offene `object`-Position möglich:

```text
list<object>[list<string>["a" "b"]]
map<string,object>{ "names": list<string>["Ada" "Alan"] }
```

Bei `object` findet keine datengetriebene Best-Common-Type-Inferenz statt. Ein unqualifiziertes inneres `[...]` bleibt daher `list<object>`; konkrete Typinformation erfordert den expliziten Decorator.

### 4.7 Well-known Symbols, TypeReferences und Definitionen

`Symbol`, `TypeReference` und `VariableDefinition` sind modellierte well-known EntityTypes. Sie können über den erwarteten Property-Typ aus skalaren beziehungsweise partiellen Formen konstruiert werden und stehen Extensions unter denselben Regeln zur Verfügung.

```text
Symbol(name: "foo")                  <- foo
TypeReference(typeSignature: "string") <- string
VariableDefinition(symbol: p)        <- p
```

Ein Scalar-to-Entity-Parser wird nur gewählt, wenn der erwartete EntityType nicht referentiell aufgelöst werden darf. Ein bloßer Identifier kann dann keine bestehende Variable oder keinen Property-Pfad meinen, sondern erzeugt eine neue partielle Entity. Der zuständige Experte vervollständigt sie.

Die Klammerrollen sind verbindlich:

```text
{first string}                         // inferred VariableDefinition
(variable-definition first string)    // expliziter EntityType
(par first string)                     // Kurzalias der expliziten Form
```

`{...}` konstruiert den erwarteten EntityType; `(...)` beginnt mit einer expliziten `TypeReference`. `VariableDefinition` bindet positional `symbol` und `type`. Deshalb lautet die normalisierte Parameterdeklaration:

```text
%(declare-instruction foo {first string} {second boolean})
%(declare-instruction greet {name string} {punctuation string default: "!"})
%(declare-instruction inferred {prefix default: "Hello"})
```

`default:` ist ausschließlich named und kann jede Expression aufnehmen. Fehlt der Typ, wird er aus dem statischen Typ des Defaults inferiert. Ohne Default ist ein Parameter durch den modellierten Initializer `required = true`; ein Default setzt ihn auf optional. `required: false` ohne Default bedeutet bei fehlendem Argument `null`. `required: true` zusammen mit `default:` ist widersprüchlich und wird bei der Completion abgewiesen. Ein explizites `default: null` bleibt als gesetzter Default erkennbar.

Die frühere Verschmelzung `first:string` ist keine Sprachform mehr. Der deklarative Präfix lautet überall getrennt `symbol type`.

Symbole werden pro Template und Namensraum interniert. Definitionen und `TemplateVariable`-Referenzen tragen dieselbe kanonische `Symbol`-Instanz; Scope- und Runtime-Lookups dürfen deshalb `IdentityHashMap` verwenden. Textnamen werden nur am Reader-Rand und bei der Re-Kanonisierung deserialisierter Modelle benötigt.

Die Completion besitzt eine Scope-Phase nach Argumentnormalisierung, aber vor dem Parsen des primären Blocks. Experten befüllen dort `VariableDefiningNode.variableDefinitions`. Bei einem `BlockNode` übernimmt der Parser diese Reflection in den primären Blockscope; bei einer blockfreien Definition wie `Var` ab der folgenden Position in den aktuellen Scope. Der Parser kennt dadurch weder `ForEach` noch eine Extension für deren Scope-Aufbau als Sonderfall.

Bei `ForEach` kann die skalare Definition unvollständig sein:

```text
%(for-each input.persons person)
  ${person.name}
%(end)
```

Der `ForEach`-Experte validiert, dass `iterable` einen statisch bekannten `CollectionType` besitzt, überträgt dessen Elementtyp in `person.type`, setzt `required=true` und `mutable=false` und veröffentlicht die vervollständigte Definition. Fehlt das zweite Argument, wird der Name `_` verwendet. Secondary Blocks erben diese Definitionen nicht; Definitionen gelten grundsätzlich für den primären Block ihres Owners.

Maps werden bewusst nicht über `ForEach` iteriert, weil ein künstlicher `Map.Entry<K,V>`-Elementtyp die getrennte statische Typinformation verwischen würde. Dafür existiert `ForEachEntry` mit unabhängig optionalen Key- und Value-Definitionen:

```text
%(for-each-entry input.peopleById key: id value: person)
  ${id}: ${person.name}
%(empty)
  no people
%(end)

%(for-each-entry input.peopleById value: person)
  ${person.name}
%(end)
```

`key` wird aus `K`, `value` aus `V` vervollständigt; beide Definitionen sind readonly und nur im primären Block sichtbar. Mindestens eine Bindung ist nicht syntaktisch erforderlich: `%(for-each-entry someMap)` ist als reine Iteration mit Seiteneffekten im Body zulässig. Der `empty`-Block erhält wie bei `ForEach` keine Laufvariablen.

Ohne erwarteten Entity- oder Map-Typ ist `{...}` ein Fehler. Explizite Entities bleiben mit `(TypeName ...)` möglich.

Unmarkierte Ganzzahlen sind `integer`, unmarkierte Fließkommazahlen `double`. Wenn der Binding-Typ keine Inferenz erlaubt, stehen insbesondere die expliziten Formen zur Verfügung:

```text
long:42
float:1.5
```

## 5. Parser, Resolver, Completion und Recovery

### 5.1 Parser-Verantwortung

`StandardTemplateParser` besitzt die Textsyntax und Blockverdrahtung. Er kennt nicht die fachliche Modellwelt im Detail. Dafür delegiert er an `TemplateParserResolver`.

Wichtige Parser-Eigenschaften:

- Root gibt einen einzelnen Node direkt zurück; `SequenceNode` nur für mehrere Root-Nodes.
- Blockmarker wie `else`, `case`, `default`, `end` werden beim Block-Wiring interpretiert.
- `#{...}` ist Kommentar, außer es ist eine bekannte Declaration wie `declare-instruction`.
- Parser unterstützt `String`, `InputStream`, `File`, `Path` und `Reader`-Einstiege.
- Fehler tragen Textpositionen.

### 5.2 Resolver-Verantwortung

`StandardTemplateApiResolver` verbindet Syntax mit Modellwelt:

- Output-Ausdrücke auflösen.
- Instructions auflösen.
- Declaration-Scope verwalten.
- Validation-Scope verwalten.
- ValueDescriptor-Typen über Registry/CmdResolver auflösen.
- Standard-Instructions direkt kennen (`if`, `for-each`, `switch`).
- Declared instruction calls zu `InvokeInstruction` normalisieren.
- Expert completion und validation anstoßen.

### 5.3 Completion

Completion und Validation sind bewusst verbunden. Der Experte darf ein geparstes Entity komplettieren, z.B.:

- explizite `typeSignature` setzen,
- `valueType`/Output-Typen prägen,
- gewöhnliche VD-Ketten einsetzen,
- Default-VD-Konversionen ergänzen,
- RuntimeTypeSpecification für declared instructions erzeugen.

Nach erfolgreicher Completion soll die Entity genug Typinformation tragen, damit spätere Framework-/Wiring-Validierung nicht wieder Expertenlogik braucht.

### 5.4 Recovery-Modi

Parser liefert:

```java
Maybe<TemplateNode>
```

Modi:

```text
STRICT
  Fehler -> Maybe.empty(ParseError)

SUBSTITUTE
  Fehlerfragment -> sichtbarer ErrorNode
  Ergebnis -> Maybe.incomplete(template, ParseError)

SILENCE
  Fehlerfragment -> stiller ErrorNode
  Ergebnis -> Maybe.incomplete(template, ParseError)
```

`SILENCE` unterdrückt Effekte, nicht Diagnostik.

`TemplateParseError` enthält Source-Position, Fragment und Reason-Kette.

## 6. Evaluation, PAI und ValueDescriptor-Backing

### 6.1 Runtime-Kontext

`TemplateEvaluationContext` stellt zur Laufzeit bereit:

```text
evaluate(ValueDescriptor)
evaluate(TemplateNode)
append(String)
withVariables(...)
defaults()
resolvedDefaults()
```

Der aktuelle Kontext liegt während der Evaluation in:

```java
ScopedValue<TemplateEvaluationContext> CURRENT
```

`RuntimeTemplateEvaluationContext.evaluate(node)` betritt diesen Scope, sodass PAI während normaler Getter-Aufrufe Zugriff auf den Kontext hat.

### 6.2 EvaluationPai

`EvaluationPai` ist der entscheidende Trick:

- liest den direkten Property-Wert,
- erkennt Property-Level-VD-Backing,
- erkennt direkte `ValueDescriptor`-Instanzen,
- evaluiert Collections und Maps rekursiv, sobald direkte oder verschachtelte VD-Werte enthalten sind,
- erhält dabei List/Set/Map-Kind und die reflektierte Parametrisierung der `PlainList`, `PlainSet` oder `PlainMap`,
- verhält sich ohne gebundenen `TemplateEvaluationContext` lenient:
  - einzelner VD-Wert -> `null`,
  - Collection/Map ohne aktuelle Evaluation -> bleibt wie sie ist.

Damit kann ein Experte normal schreiben:

```java
for (Object operand : concat.getOperands()) {
    result.append(operand);
}
```

und bekommt bei aktiver Evaluation bereits die evaluierten Operanden.

Wichtig: Bei reflected normalisierten `ValueDescriptor`-Entities wird `EvaluationPai` an die Entity gehängt (`EnhancedEntity.pushPai(...)`). Dadurch wird etwa `Concat.operands` beim Getter evaluiert. Der `ConcatEvaluator` muss nicht selbst in Collections nach VD-Elementen suchen; er darf dem Property-Return vertrauen.

### 6.3 Variable und gestaffelter TemplatePropertyPath

`RuntimeTemplateEvaluationContext` kennt u.a.:

```text
TemplateVariable
TemplatePropertyPath
  root: ValueDescriptor
  accesses: list<PathAccess>
  type: TypeReference
```

Der Template-Parser verwendet nicht mehr den textbasierten BVD-`PropertyPath`. `TemplateVariable` trägt das kanonische Symbol und liest per Identity-Key aus dem Runtime-Scope. `TemplatePropertyPath` evaluiert seinen Root-VD und danach eine bereits validation-time aufgelöste Kette modellierter Zugriffe.

```text
PathAccess
├── PropertyAccess
├── ListIndexAccess
└── MapKeyAccess

PropertyAccess
  property: PropertyReference
  optional: boolean
  resultType: TypeReference

ListIndexAccess
  index: object                 // statisch integer-typisierte Expression
  optional: boolean
  resultType: TypeReference

MapKeyAccess
  key: object                   // gegen den reflektierten Map-Key-Typ geprüft
  optional: boolean
  resultType: TypeReference

PathAccess
  sourceRange: TextRange

PropertyReference
  symbol: Symbol
  declaringType: TypeReference
  type: TypeReference
  resolvedProperty: Property @Transient
```

Der transportable Teil enthält damit alle Namen und Typinformationen. `resolvedProperty` cached die Reflection-Property transient; nach Deserialisierung wird sie einmal aus `declaringType` und dem kanonischen Symbol rekonstruiert. Die Runtime interpretiert keinen Pfadstring erneut.

Normales `.` ist strikt; ein `null`-Empfänger erzeugt eine modellierte `NullPathElement`-Reason. `?.` ist nur für R-Values erlaubt und wird als `PropertyAccess.optional` modelliert:

```text
input.person.address.city       // strikt
input.person?.address?.city     // optionale Navigation
input.people[0].name            // statisch typisierter Listenzugriff
input.people[index].name        // Index darf eine Expression sein
input.peopleById["ada"].name   // Map-Key gegen K typisiert
input.people?[9]?.name          // optionaler Indexzugriff
```

Unbekannte Properties bleiben auch mit `?.` statische Parsefehler. Optionale Navigation ist in L-Values verboten, damit `set` niemals unbemerkt zu einem No-op wird.

`[]` ist abhängig vom reflektierten Empfängertyp `ListIndexAccess` oder `MapKeyAccess`; Sets sind nicht indexierbar. Index und Key sind gewöhnliche Expressions. Listenindizes müssen statisch und zur Runtime tatsächlich `integer` sein; Map-Keys müssen zu `K` passen. `?[...]` ist die optionale R-Value-Form: Ein nulliger Receiver, ein ungültiger Listenindex oder ein fehlender Map-Key liefert `null`. Normales `[...]` ist strikt und erzeugt bei denselben Fällen eine modellierte Reason. Damit bleibt ein vorhandener Map-Eintrag mit Wert `null` von einem fehlenden Key unterscheidbar. L-Values unterstützen Property-, List- und Map-Zugriffe, verbieten aber weiterhin jede optionale Access-Stufe; ein finaler Map-Zugriff schreibt mit `Map.put`, während ein fehlender intermediärer Key strikt fehlschlägt.

`TemplatePropertyPath.sourceRange` transportiert die gesamte Expression bis zur Runtime; jede einzelne `PathAccess` trägt zusätzlich ihren exakten Quellbereich. `PathEvaluationError` und `NullPathElement` enthalten den vollständigen Pfad, das konkrete Segment, die Operation (`read`/`write`) und genau diesen `TextRange`. Strikte Runtime-Fehler sind dadurch sowohl für Menschen lokalisierbar als auch strukturiert durch Werkzeuge auswertbar.

`PropertyAssignmentTarget` enthält denselben `TemplatePropertyPath` wie ein R-Value. Lesen und Schreiben teilen damit Root-, Symbol-, Typ-, Range- und Reflection-Information. Ein `set` auf dem letzten `PropertyAccess`, `ListIndexAccess` oder `MapKeyAccess` schreibt entsprechend per Property, `List.set` oder `Map.put`; Zwischenzugriffe werden mit derselben strikten Pfadsemantik ausgewertet.

### 6.4 Runtime-Scope und lexical scope

Es gibt zwei korrespondierende Scope-Systeme:

```text
TemplateValidationScope
  name -> (GenericModelType, mutable)

AbstractScopedTemplateEvaluationContext
  name -> (runtime value, mutable)
```

Nested Blöcke sehen Variablen der Umgebung. Variablen, Parameter und Loop-Variablen leaken nicht aus ihrem Block heraus.

Bei `ForEach` werden Elementvariable und optionaler Index nur im primären Block sichtbar, nicht im `empty`-Block.

Bei declared instructions werden Parameter nur im Body der Declaration sichtbar und beim Invoke als temporäre Runtime-Variablen projiziert.

## 7. Experten-Registry

`TemplateExpertRegistry` ist die zentrale Extension-Stelle:

```text
registerEvaluator(EntityType<N>, TemplateNodeEvaluator<N>)
registerVdEvaluator(EntityType<V>, VdEvaluator<V, O>)
registerConversion(EntityType<V>, inType, outType, ValueConversion<I,V,O>)
registerDefaultConversion(...)
```

`ConfigurableTemplateExpertRegistry` sammelt:

- Node-Evaluatoren,
- VD-Evaluatoren,
- typisierte VD-Konversionskanten,
- Default-Konversionen,
- sichtbare VD-Typen für Parser-Auflösung.

`StandardTemplateExperts` registriert die Basisausstattung.

Wichtige Expertentypen:

```text
TemplateNodeEvaluator<N>
  evaluate(context, node)
  validate(validationContext, node)

VdEvaluator<V,O>
  transform(context, descriptor)
  validate(validationContext, descriptor)

ValueConversion<I,V,O>
  convert(context, input, descriptor)
  validate(validationContext, descriptor)
```

Experten sollten Property-Zugriffe über `PropertyLiteral` machen, nicht jedes Mal String-Lookups wiederholen.

## 8. PropertyLiteral

`PropertyLiteral` liegt in `gm-core-api` und wird so verwendet:

```java
PropertyLiteral name = PropertyLiteral.of(T, "name");
```

Es ist ein kleiner lazy Lookup Holder:

```text
property() : Property
name()     : String
implements CharSequence
```

Das Ziel ist:

- Property-Namen neben dem Modelltyp deklarieren,
- Experten von wiederholten String-Lookups befreien,
- manuelle statische `Property`-Felder vermeiden,
- eine Java-Annäherung an echte hiconic Property-Literale schaffen.

## 9. API und Factory-Konzept

Die öffentliche Einstiegsebene ist zweistufig/mehrstufig:

```text
TemplateFactories
  -> TemplateFactory / TemplateFactoryBuilder
  -> TypedTemplateFactory<I>
  -> Template<I>
```

Prinzip:

1. Eine immutable ExpertFactory/TemplateFactory registriert Standardexperten.
2. Über `derive`/Builder können weitere Experten additiv ergänzt werden.
3. Danach wird ein Root-Typ festgelegt.
4. Dann wird geparst.
5. Das Ergebnis ist ein `Template<I>` mit typisiertem Input.

Typisierte Root-Einstiege:

```text
withRoot(EntityType<E>)
withRoot(GenericModelType)
withRoot(Class<I>)
withListRoot(...)
```

Convenience-Factories:

```text
TemplateFactories.base()
TemplateFactories.html()
TemplateFactories.xml()
...
```

Die vordefinierten Factories sollen lazy aufgebaut werden. Jede Factory soll aus einer bestehenden immutable Konfiguration ableitbar sein, ohne diese zu mutieren.

Root-Variable:

```text
input
```

`input` ist der aktuelle Default-Name für den Root-Wert. Er sollte vermutlich konfigurierbar bleiben; meistens reicht aber auch ein `Var`/alias im Template.

## 10. Defaults, Locale, Zone und Formatierung

`TemplateEvaluationDefaults` ist modelliert. Modellierte Property-Defaults werden bereits ganz normal durch `EntityType.create()` angewendet; der Parser besitzt dafür keine Sonderlogik. Aktuelle relevante Defaults:

```text
defaultLocale
defaultZone
defaultDateFormat
defaultNumberFormat
```

`FormatDate` und `FormatNumber` können Defaults nutzen, aber auch explizite Properties setzen. Zone-Aspekte bei Date/Time sind wichtig: ein `Date`/Instant-artiger Wert muss bei Formatierung immer mit klarer Zone interpretiert werden.

Die verbleibende Frage betrifft nicht das Defaulting, sondern nur abgeleitete JVM-Typen wie `Locale`, `ZoneId`, `DateTimeFormatter` und `NumberFormat`. Voll modellierte Defaults sind serialisierbar, aber nicht direkt mit diesen JVM-Objekten typisiert.

Aktueller Design-Kompromiss:

- Modellierte Defaults bleiben die transportable Quelle der Wahrheit.
- Aufgelöste JVM-Objekte dürfen gecached werden.
- Ein generischer transient-property-cache auf den modellierten Config-Entities ist wahrscheinlich besser als ein immer spezifischeres `ResolvedTemplateDefaults`.

Offenes Ziel:

```text
modeled defaults as serializable configuration
+ transient cached JVM values for hot runtime access
+ default methods as convenience/cache accessors where dependency-neutral
```

Dabei kritisch bleiben: Transient Properties sind ein offizielles GM-Feature, aber sie koppeln Runtime-Caches an Modellinstanzen. Das ist hier sinnvoll, solange transportable Properties weiterhin autoritativ bleiben.

## 11. Type Resolution und CmdResolver

Sichtbarkeit von Typen kommt über `CmdResolver`:

```text
CmdResolver
  -> ModelOracle
  -> MetaData resolver
```

Damit können vollqualifizierte und kurzqualifizierte Typnamen aufgelöst werden, solange sie eindeutig sind.

Regeln:

- Explizite Entities in Lisp-artiger Form beginnen mit einem Typ-Identifier; `{...}` inferiert Entity- oder Map-Typ aus dem Binding-Ziel.
- Enum-Konstanten nutzen `::`.
- Sonstige Identifier sind Variablen plus optionaler PropertyPath.
- Explizite `Alias`-MD darf jeden konkreten EntityType benennen und wird auch für gewöhnliche Entity-Payloads aufgelöst.
- Automatisch abgeleitete Lower-kebab-Aliase gelten nur für funktionale Typen wie VDs und Directives.
- Ambiguität ist ein Parse-/Resolution-Fehler.

Die Parserlogik enthält keine fachliche Sonderregel für Formatierung oder Escaping. Wenn `(format-number value "0.00" locale: "de-DE")` funktioniert, dann weil:

- `FormatNumber` modellierte Properties hat,
- `@PositionalArguments`/Metadata die Bindung beschreibt,
- der Parser reflektiert bindet,
- der Experte auswertet.

Parser-Spezialfälle sind nur für Grammatik und Blockstruktur akzeptabel, nicht für fachliche Parametersemantik.

## 12. Wichtige geklärte Hürden

### 12.1 Modell vs. Experte

Das Modell soll nicht „intelligent“ im Sinne komplexer Rückgabetyp-Inferenz sein. Statische `valueType()`-Fälle sind ok, aber intelligente Completion gehört zum Experten.

Ergebnis:

```text
parse entity
-> expert completion/enrichment
-> explicit type information on entity
-> normal framework validation/wiring
```

### 12.2 DynamicEntityType verworfen

Für declared instructions wurde `DynamicEntityType` nicht benutzt. Runtime-Signaturen werden modelliert über `RuntimeTypeSpecification` und `RuntimePropertySpecification`.

Das erhält Transportierbarkeit und vermeidet eine halb-reflektierte, nicht sauber im Modellraum verankerte Parallelwelt.

### 12.3 Declared instruction Scopes

Parameter sind während des Declaration-Body sichtbar, aber nicht darüber hinaus. Der Invoke erzeugt einen temporären Runtime-Scope. Nested Blöcke sehen Elternvariablen, aber leaken nicht zurück.

### 12.4 PAI als natürliche Laufzeitbrücke

Die wichtigste technische Erkenntnis: Der Template-AST muss nicht überall `ValueDescriptor`-Properties tragen. Normale Properties bleiben normal typisiert. PAI macht VD-Backing beim Getter transparent.

Das ist nicht Bequemlichkeit, sondern Architektur: reflektive Modelle bleiben sauber und die reaktive/dynamische Laufzeit wird an einer schmalen, systematischen Stelle verankert.

### 12.5 Output Safety durch Typ, nicht Disziplin

`OutputNode.output : SafeOutput` erzwingt, dass Output-Sicherheit im Modell sichtbar ist. `NoEscape` ist weiterhin möglich, aber explizit und policy-kontrolliert.

### 12.6 Positional collection args

Die variadic-artige letzte Collection-Property ist geklärt:

```text
Wenn letzte positional property eine Collection ist:
  alle folgenden positional args bis zum nächsten named arg sind Elemente.
```

Das macht VDs wie `Concat`, `And`, `Or` natürlich formulierbar. Ein explizites lineares Collection-Literal (`[...]`, `list<T>[...]`, `set<T>[...]`) bindet dagegen die Collection-Property als Ganzes und wird nicht noch einmal als einzelnes variadisches Element verpackt. Der Binder erzeugt für die variadische Form denselben reflektierten List- oder Set-Typ wie für die Literalform.

Der erwartete Argumenttyp darf außerdem vom zuständigen Node-Experten abhängig vom bereits normalisierten Owner präzisiert werden. Dadurch kann etwa `Var.value` den deklarierten Variablentyp, `Set.value` den aufgelösten L-Value-Typ und `VariableDefinition.default` ihren expliziten Typ als Binding-Ziel an den allgemeinen Expression-Parser weitergeben. Unqualifizierte Collection-Literale funktionieren so auch an modelltechnisch offenen `object`-Properties, ohne dass der Parser die konkrete Instruction als Sonderfall nachbauen muss.

## 13. Stand der Implementierung

Implementiert und durch Unit Tests abgesichert:

- Node-Modell inkl. `TextNode`, `SequenceNode`, `CommentNode`, `ErrorNode`, `OutputNode`.
- Instructions: `If`, `ForEach`, `ForEachEntry`, `Switch`, `Set`, `Append`, `Insert`, `Add`, `Put`, `Remove`, `InvokeInstruction`.
- Declaration-Modell: `DeclareInstruction`, `Var`, Runtime-Argument-Spezifikation.
- Parser mit Positionen, Recovery-Modi und Block-Wiring.
- Parser-Einstiege für String/InputStream/File/Path/Reader.
- Allgemeine VD-Pipeline in jedem Value-Kontext und Default-VD-Konversionen für Output.
- VD-Escaper für HTML, XML, JSON, JavaScript, CSS, URL component, Java literal und NoEscape.
- `FormatDate` und `FormatNumber` mit Defaults/Explicit Properties.
- Boolean-/Comparison-/Arithmetic-VDs.
- Reflection-VDs `TypeOf`, `DeclaredTypeOf`, `Is` und `AssignableTo`.
- `Concat`.
- `EvaluationPai` inklusive Collection-/Map-VD-Evaluation.
- Declared instruction invocation über `InvokeInstruction`.
- Factory/API-Schicht mit typisiertem Root.
- Tests in `modeled-templates-test`.

Zuletzt validiert:

```text
modeled-templates hc install
modeled-templates-test hc compile
modeled-templates-test hc test
77 TESTS, ALL UNIT TESTS WERE SUCCESSFUL.
```

## 14. Nutzungsskizzen

### 14.1 HTML Template mit Entity-Root

```java
Template<Person> template =
    TemplateFactories.html()
        .withRoot(Person.T)
        .parse("Hello ${input.name}")
        .get();

String result = template.evaluateToString(person);
```

`input.name` wird als gestaffelter `TemplatePropertyPath` modelliert, typisiert, automatisch nach `HtmlOutput` transformiert und escaped.

### 14.2 Reader Support

```java
try (Reader reader = ...) {
    Template<Person> template =
        TemplateFactories.html()
            .withRoot(Person.T)
            .parse(reader)
            .get();
}
```

### 14.3 Declared instruction

```text
%(declare-instruction greet {name string})
<b>${name}</b>
%(end)

<h1>%(greet "Dirk")</h1>
```

Erwarteter Output im HTML-Profil:

```html
<h1><b>Dirk</b></h1>
```

### 14.4 Concat

```text
${(concat "Hello " input.title "!")}
```

`Concat.operands` ist `List<Object>` und wird durch PAI elementweise evaluiert. Der `ConcatEvaluator` vertraut dem Property-Return.

### 14.5 VS-Code-Syntax und eingebettete Hostsprachen

Unter `vscode-modeled-templates` liegt eine deklarative TextMate-Extension. Neben reinen `.mt`-Dateien bietet sie Hostvarianten, bei denen die normale Hostgrammatik aktiv bleibt und MT-Konstrukte mit höherer Priorität injiziert werden:

```text
*.html.mt       HTML + MT
*.json.mt       JSON + MT
*.yaml.mt       YAML + MT
*.xml.mt        XML + MT
*.md.mt         Markdown + MT
*.css.mt        CSS + MT
*.js.mt         JavaScript + MT
*.ts.mt         TypeScript + MT
```

Hervorgehoben werden `%()`-Directives, `${}`-Outputs, `#{}`-Kommentare, Entityformen, named arguments, Collection-Type-Decorators, Enum-Literale, Property-Pfade, optionale Zugriffe, Number-Suffixe und allgemeine Pipes. `demo/demo.html.mt` und `demo/demo.json.mt` sind unmittelbar vorführbare Beispiele. Die Extension enthält keinen Runtime-Code.

## 15. Konsolidierter Backlog

Der gepflegte, priorisierte Backlog steht in [modeled-template-open-issues.md](modeled-template-open-issues.md). Die folgenden Unterabschnitte dokumentieren überwiegend bereits gelöste Detailentscheidungen und bleiben als technische Vertiefung erhalten; sie sind nicht pauschal als offene Arbeit zu lesen.

### 15.0 Kompakter Wiedereinstieg

Für einen Kontext- oder Rechnerwechsel zuerst [modeled-template-handover.md](modeled-template-handover.md) lesen. Er trennt normative Festlegungen, Implementierungsstand und echte offene Punkte von der Entstehungsgeschichte dieser Tiefenreferenz.

### 15.1 Whitespace handling

Whitespace-Kontrolle ist als gemeinsame Property von `DirectiveNode` modelliert:

```text
DirectiveNode.whitespace : WhitespacePolicy

WhitespacePolicy
  before : WhitespaceAction
  after  : WhitespaceAction

WhitespaceAction
  preserve
  trim
  trimLine
```

Die Policy wird wie jede andere Property gebunden; `{...}` inferiert dabei `WhitespacePolicy`:

```text
%(some-thing "Hello"
    fast: true
    whitespace: { before: ::trimLine after: ::trimLine })
```

`preserve` ist der Default. `trim` entfernt sämtlichen angrenzenden Whitespace. `trimLine` entfernt Einrückung und genau den angrenzenden Zeilenumbruch, sodass eine alleinstehende Instruction-Zeile keinen Leerraum hinterlässt.

### 15.2 JVM-Type Cache für modellierte Konfiguration

Die Defaults selbst sind durch `EntityType.create()` vollständig normalisiert. `ResolvedTemplateDefaults` betrifft lediglich deren JVM-Auflösung und ist vermutlich noch zu spezifisch. Besser wäre ein generischer transient-property-cache auf modellierten Config-Entities:

```text
modeled serializable config
-> lazy resolved JVM object
-> transient property cache
```

Das betrifft besonders:

```text
Locale
ZoneId
DateTimeFormatter
DecimalFormatSymbols
NumberFormat
```

### 15.3 Collection-, Map-, Entity- und Null-Literale

List-, Set-, Map- und Entity-Literale werden aus ihrem Binding-Ziel inferiert oder durch einen expliziten Typdecorator qualifiziert. Sie materialisieren als typtragende PlainCollections. Das `null`-Literal bleibt während der Bindung durch einen internen Marker von „nicht angegeben“ unterscheidbar und wird erst im Modellwert zu Java-`null`; dadurch funktionieren `Mandatory`, Defaults und explizites Null gleichzeitig korrekt.

VD-Werte werden nach completion-driven Typwissen validiert, nicht nach ihrer Java-Klasse. Bei Collections gilt `null` unabhängig vom Element-, Key- oder Value-Typ als assignable; Constraints und Nullability des umgebenden Modells bleiben die fachliche Schranke. Direkt verschachtelte Collection-Typen bleiben verboten, außer die betreffende äußere Position ist bewusst als `object` geöffnet.

Die wesentlichen Collection-Kanten sind umgesetzt: rekursive VD-Materialisierung unter Erhalt des PlainCollection-Typs, typgerechte variadische Sets, Duplicate-Key-Prüfung für direkt bekannte Map-Literale, strikter versus optionaler Map-Key-Zugriff und getrennte `ForEachEntry`-Iteration mit optionalen, aus `K` und `V` inferierten Laufvariablen.

Die kontextfreie Form einer `TypeReference` ist `(type Person)`; `(T Person)` ist ihr kompakter Alias. Beides ist normale explizite Entity-Syntax, keine zusätzliche Literalgrammatik. In einer modelliert `TypeReference` erwartenden Property genügt weiterhin der nackte Typname (`Person`, `list<string>`). Insbesondere erhält `Eq` keine versteckte, reihenfolgeabhängige Inferenz.

### 15.4 Metadata für Parser-Bindung

`@PositionalArguments` und die auf Typen auflösbare `Alias`-MD bilden den Anfang. Es sollte weiter modelliert werden:

```text
aliases
required/optional
default values
varargs/collection semantics
documentation/help
deprecation
parser hints
```

Langfristig sollte der Parser möglichst vollständig aus Reflection + Metadata binden.

Der gemeinsame Binder prüft unbekannte und doppelte Properties, positional/named ordering, Variadic-Elementtypen, Nullability und Assignability. Die Completion vervollständigt verschachtelte VDs und Experten prüfen weitere fachliche Invarianten. Direkt danach läuft einmalig ein allgemeiner, CMD-basierter Constraint-Pass für `Mandatory`, `Pattern`, `Min`, `Max`, `MinLength` und `MaxLength`; bei `Min` und `Max` wird auch `exclusive` respektiert.

Der Pass validiert ausschließlich parse-time bekannte Direktwerte. Ist eine Property VD-backed, wird ihr späterer Wert bewusst nicht gegen wertabhängige Metadata geprüft: Er ist erst zur Runtime bekannt und darf beim Parsen weder geraten noch fälschlich moniert werden. Die Struktur des Deskriptors selbst wird weiterhin completed und validiert. Metadata wird über die am `TemplateFactory` konfigurierten Input- und Experten-`CmdResolver` aufgelöst, sodass Modell-Metadata, Selektoren und Extensions denselben Mechanismus verwenden.

### 15.5 Better diagnostics

Der Tokenizer erhält Offsets, aus denen der Parser verschachtelungsfest absolute `TextRange`s erzeugt. Der Binder ordnet den Range des tatsächlichen Werts seiner reflektierten Property zu. Completion- und Constraint-Verletzungen sind dadurch modellierte `TemplateParseError` mit einem navigierbaren `modelPath` wie `Var.value.code` und dem exakten Argument- oder Literalbereich statt nur der umgebenden Directive. Collection-/Map-Literalwerte, verschachtelte Entities und Pipeline-Stufen propagieren ihre Teilbereiche.

Property-Path-Runtime-Reasons enthalten vollständigen Pfad, Segment, Operation und den Range der konkreten `PathAccess`. Weitere Diagnosequalität kann noch wachsen:

```text
expected type
actual type
model path
suggested fix
ambiguous candidates
visible variables/types at position
```

### 15.6 Template graph serialization and transport

Da der AST ein GM-Graph ist, sollte Serialisierung/Transport explizit getestet werden:

- parsed template speichern,
- später ohne Source evaluieren,
- declared instructions und runtime argument specs transportieren,
- transient caches nicht serialisieren,
- experts beim Laden wieder passend binden.

### 15.7 Custom instruction metadata

Framework-/Usage-defined Instructions sind konkrete `InstructionNode`-Types. Declared instructions sind runtime-spec-basiert. Für Custom Instructions braucht es noch klarere Registratur-/Metadata-Konventionen:

- lower-kebab alias,
- block properties,
- secondary block markers,
- positional args,
- expected scopes für Blocks.

### 15.8 Security policies

`NoEscape` ist explizit und erlaubt, aber Policy sollte konfigurierbar bleiben:

```text
allowNoEscape
allowed output contexts
input mutation policy
custom transformer allowlist
```

### 15.9 Performance

Wichtige Performance-Punkte:

- `PropertyLiteral` statt wiederholtem String-Lookup.
- Completion soll Typen materialisieren, damit Runtime nicht inferiert.
- Default-Konversionspfade sollten gecached werden.
- Parsed Template sollte wiederverwendbar sein.
- PAI-Collection-Cloning evaluiert lazy erst ab erstem VD-Element; das ist gut, aber tiefe Strukturen brauchen Profiling.

### 15.10 Dokumentation und Beispiele

Aus diesem Handover sollten später entstehen:

- User-facing syntax guide.
- Expert authoring guide.
- Model extension guide.
- Security/output safety guide.
- Parser diagnostics guide.
- Cookbook für HTML/XML/JSON/YAML/CSS.

## 16. Mentales Modell für Weiterarbeit

Wenn eine neue Funktion gebaut wird, zuerst fragen:

```text
Ist es Syntax?
  -> Parser/Block-Wiring

Ist es ein Wert?
  -> ValueDescriptor + VdEvaluator

Ist es eine Wertoperation, Konversion, Formatierung oder Escaping?
  -> normaler ValueDescriptor + VdEvaluator/ValueConversion

Ist es ein modelliertes Template-Directive?
  -> DirectiveNode plus orthogonale Rollen Instruction/Declaration/Block/Silent

Ist es template-lokal deklariert?
  -> DeclareInstruction + RuntimeTypeSpecification + InvokeInstruction

Ist es Konfiguration?
  -> modellierte Defaults + ggf. transient JVM cache
```

Die Architektur steht und fällt damit, dass diese Ebenen nicht vermischt werden. Genau darin liegt der Gewinn gegenüber klassischen Template-Engines: Die Sprache ist nur eine Projektion auf ein reflektives, validierbares, transportierbares Modell.

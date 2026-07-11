# Hiconic Template System — konsolidierte Architektur- und Agentenreferenz

Stand: 2026-07-11

Dieses Dokument ist als dichter Wiedereinstieg gedacht: ein Mensch oder ein Codex-Agent soll damit auf einer anderen Maschine verstehen, was hier gebaut wurde, welche Architekturentscheidungen gelten, welche Syntax der Parser versteht, wie GM/hiconic-Modelle, ValueDescriptoren, Experten, PAI und declared instructions zusammenspielen und welche offenen Enden als nächstes wichtig sind.

## 1. Kurzdefinition

Ein hiconic Template ist kein String mit Helferfunktionen, sondern ein reflektierter GM-Modellgraph aus `TemplateNode`-Entitäten. Normale, typisierte Modell-Properties können durch `ValueDescriptor`-Backing dynamisch sein. Während der Evaluation sorgt eine `PropertyAccessInterceptor`-Schicht dafür, dass Getter wie `ifNode.getCondition()` oder `concat.getOperands()` bereits den evaluierten Wert liefern. Parser, Completion, Validierung und Transformer-Planung stellen vorher sicher, dass diese Werte zu den deklarierten GM-Typen passen — besonders zu `SafeOutput` bei `${...}`.

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
  instr.InstructionNode
    instr.BlockInstructionNode
      instr.If
      instr.ForEach
      instr.Switch
    instr.Set
    instr.InvokeInstruction
  decl.DeclarationNode
    decl.Var
    decl.DeclareInstruction
```

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

### 3.3 Transformer

Transformer sind normale GM-Entitäten unter `core.output.Transformer`.

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
```

Transformer werden durch `TemplateValueTransformer<I, P, O>`-Experten ausgewertet und können in Pipelines kombiniert werden. Sie sind nicht nur Escaper, sondern allgemeine typisierte Konversionen:

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

String:
  Concat

Pipeline:
  TransformValue
```

Die Evaluatoren implementieren `VdEvaluator<V, O>`.

Arithmetik ist absichtlich über `Object`-Properties modelliert, weil `Number` keine GM-Abstraktion ist. Type-Checks laufen über `ValidationContext.getType(entity, property)` und beachten ValueDescriptor-Backing.

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

## 4. Syntax

### 4.1 Hauptformen

```text
${...}    Output expression, wird zu SafeOutput vervollständigt
%{...}    Block instruction oder interner Blockmarker
%{...}%   blockfreie runtime instruction
#{...}    Declaration oder Kommentar
(...)     ValueDescriptor/entity-artige Lisp-Form in Expressions
|         Transformer-Pipeline in Output-Ausdrücken
```

Beispiele:

```text
Hello ${input.name}

%{if input.active}
  ${input.name}
%{else}
  inactive
%{end}
```

```text
#{declare-instruction greet name:string}
<b>${name}</b>
#{end}

<h1>%{greet "Dirk"}%</h1>
```

Das zweite Beispiel wird so interpretiert:

1. `#{declare-instruction ...}` erzeugt `DeclareInstruction`.
2. Während des Blocks ist `name : string` im Validierungsscope sichtbar.
3. Nach Completion wird die Deklaration im lexical declaration scope registriert.
4. `%{greet "Dirk"}%` wird zu `InvokeInstruction`.
5. Der generic Invocation-Evaluator projiziert `RuntimeArguments` in einen temporären Runtime-Scope und evaluiert den Body.

### 4.2 Output-Pipelines

```text
${input.title}
${input.birthday | format-date "yyyy/MM/dd"}
${input.birthday | format-date "yyyy/MM/dd" | url-component-escape}
${input.title | no-escape}
```

Die Pipeline ist typisiert. Explizite Transformer werden über reflektierte Properties und `@PositionalArguments`/named args gebunden. Fehlende Konversionen werden über registrierte Default-Transformer ergänzt, wenn es einen eindeutigen Pfad gibt.

HTML-Profil-Beispiel:

```text
input.birthday : Date

${input.birthday}
=> Date -> FormatDate(defaults) -> String -> HtmlEsc -> HtmlOutput

${input.birthday | url-component-escape}
=> Date -> FormatDate(defaults) -> String -> UrlComponentEscape -> UrlComponentOutput
```

### 4.3 Function/VD-Namen

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
(Person --name "Ada")      // normale Entity, richtig gecased
```

Enums werden explizit namespaced:

```text
SomeEnum::constant
::constant                 // nur wenn der Zieltyp eindeutig bekannt ist
```

Variablen haben Vorrang als Identifier. Enum-Konstanten ohne Namespace werden nicht mit Variablen vermischt.

### 4.4 Literale und Escaping

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

Ungetypte Literale werden soweit möglich inferiert. Collection-Literale und tiefe Literal-Inferenz sind konzeptionell angelegt; bei Weiterarbeit bitte prüfen, welche Varianten aktuell vollständig implementiert und getestet sind.

### 4.5 Positional und named arguments

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
--pattern "yyyy/MM/dd"
pattern="yyyy/MM/dd"
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
- `TransformValue`-Ketten einsetzen,
- Default-Transformer ergänzen,
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
- evaluiert Collections und Maps elementweise, sobald VD-Elemente enthalten sind,
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

### 6.3 Variable und PropertyPath

`RuntimeTemplateEvaluationContext` kennt u.a.:

```text
Variable
PropertyPath
```

`Variable` liest aus dem aktuellen Runtime-Scope. `PropertyPath` evaluiert erst das Entity-Descriptor-Ziel und läuft dann reflektiert über die Property-Segmente.

### 6.4 Runtime-Scope und lexical scope

Es gibt zwei korrespondierende Scope-Systeme:

```text
TemplateValidationScope
  name -> GenericModelType

AbstractScopedTemplateEvaluationContext
  name -> runtime value
```

Nested Blöcke sehen Variablen der Umgebung. Variablen, Parameter und Loop-Variablen leaken nicht aus ihrem Block heraus.

Bei `ForEach` werden Elementvariable und optionaler Index nur im primären Block sichtbar, nicht im `empty`-Block.

Bei declared instructions werden Parameter nur im Body der Declaration sichtbar und beim Invoke als temporäre Runtime-Variablen projiziert.

## 7. Experten-Registry

`TemplateExpertRegistry` ist die zentrale Extension-Stelle:

```text
registerEvaluator(EntityType<N>, TemplateNodeEvaluator<N>)
registerVdEvaluator(EntityType<V>, VdEvaluator<V, O>)
registerTransformer(EntityType<T>, inType, outType, TemplateValueTransformer<I,T,O>)
registerDefaultTransformer(...)
```

`ConfigurableTemplateExpertRegistry` sammelt:

- Node-Evaluatoren,
- VD-Evaluatoren,
- Transformer-Bindings,
- Default-Transformer,
- sichtbare VD-/Transformer-Typen für Parser-Auflösung.

`StandardTemplateExperts` registriert die Basisausstattung.

Wichtige Expertentypen:

```text
TemplateNodeEvaluator<N>
  evaluate(context, node)
  validate(validationContext, node)

VdEvaluator<V,O>
  transform(context, descriptor)
  validate(validationContext, descriptor)

TemplateValueTransformer<I,P,O>
  transform(context, params, input)
  validate(validationContext, params, input)
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

`TemplateEvaluationDefaults` ist modelliert. Aktuelle relevante Defaults:

```text
defaultLocale
defaultZone
defaultDateFormat
defaultNumberFormat
```

`FormatDate` und `FormatNumber` können Defaults nutzen, aber auch explizite Properties setzen. Zone-Aspekte bei Date/Time sind wichtig: ein `Date`/Instant-artiger Wert muss bei Formatierung immer mit klarer Zone interpretiert werden.

Problem: JVM-Typen wie `Locale`, `ZoneId`, `DateTimeFormatter`, `NumberFormat` sind keine GM-Typen. Voll modellierte Defaults sind serialisierbar, aber nicht direkt mit diesen JVM-Objekten typisiert.

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

- Entities in Lisp-artiger Form beginnen mit einem Typ-Identifier.
- Enum-Konstanten nutzen `::`.
- Sonstige Identifier sind Variablen plus optionaler PropertyPath.
- Lower-kebab aliases gelten nur für funktionale Typen wie VDs, Instructions, Transformer.
- Ambiguität ist ein Parse-/Resolution-Fehler.

Die Parserlogik soll nicht für jeden Transformer eine Sonderregel bekommen. Wenn `format-number "0.00" --locale "de-DE"` funktioniert, dann weil:

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

Das macht VDs wie `Concat`, `And`, `Or` natürlich formulierbar.

## 13. Stand der Implementierung

Implementiert und durch Unit Tests abgesichert:

- Node-Modell inkl. `TextNode`, `SequenceNode`, `CommentNode`, `ErrorNode`, `OutputNode`.
- Instructions: `If`, `ForEach`, `Switch`, `Set`, `InvokeInstruction`.
- Declaration-Modell: `DeclareInstruction`, `Var`, Runtime-Argument-Spezifikation.
- Parser mit Positionen, Recovery-Modi und Block-Wiring.
- Parser-Einstiege für String/InputStream/File/Path/Reader.
- Output-Pipeline mit default und expliziten Transformern.
- Escaper/Transformer für HTML, XML, JSON, JavaScript, CSS, URL component, Java literal, NoEscape.
- `FormatDate` und `FormatNumber` mit Defaults/Explicit Properties.
- Boolean-/Comparison-/Arithmetic-VDs.
- `Concat`.
- `EvaluationPai` inklusive Collection-/Map-VD-Evaluation.
- Declared instruction invocation über `InvokeInstruction`.
- Factory/API-Schicht mit typisiertem Root.
- Tests in `modeled-templates-test`.

Zuletzt validiert:

```text
modeled-templates hc install
modeled-templates-test hc test
ALL UNIT TESTS WERE SUCCESSFUL.
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

`input.name` wird als `PropertyPath` modelliert, typisiert, automatisch nach `HtmlOutput` transformiert und escaped.

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
#{declare-instruction greet name:string}
<b>${name}</b>
#{end}

<h1>%{greet "Dirk"}%</h1>
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

## 15. Offene Enden und nächste Baustellen

### 15.1 Whitespace handling

Es fehlt eine saubere Syntax/Policy für Whitespace-Kontrolle:

```text
trim before block marker
trim after block marker
strip declaration-only lines
preserve literal whitespace by default
```

Zu klären ist eine Syntax, die nicht mit GM-Ausdruckssyntax kollidiert, z.B. Marker-Modifier an `${- ... -}` / `%{- ... -}` oder eine modellierte Parseroption.

### 15.2 Modellierte Defaults und JVM-Type Cache

`ResolvedTemplateDefaults` ist vermutlich noch zu spezifisch. Besser wäre ein generischer transient-property-cache auf modellierten Config-Entities:

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

### 15.3 Collection-, Map- und Entity-Literale härten

Collection-Inferenz, leere Collections, typed null, nested collections, Map-Key-Evaluation und VD-Substitution in allen Literalpositionen müssen weiter gehärtet und getestet werden.

Wichtig:

- VD-Elemente werden nach completed `valueType()` typisiert, nicht nach Java-Klasse.
- Map-Keys dürfen zur Materialisierung nicht als mutable VD-holder-Objekte im Key landen.
- Zieltyp soll Inferenz unterstützen, aber Fehlerpositionen müssen elementgenau bleiben.

### 15.4 Metadata für Parser-Bindung

`@PositionalArguments` ist der Anfang. Es sollte weiter modelliert werden:

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

### 15.5 Better diagnostics

Reason-Bäume existieren, aber Diagnosequalität kann wachsen:

```text
expected type
actual type
model path
source excerpt
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
- Transformer-Pfade sollten gecached werden.
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

Ist es Output-Konversion/Escaping?
  -> Transformer + TemplateValueTransformer + registry binding

Ist es ein Template-Knoten mit Runtime-Effekt?
  -> InstructionNode/TemplateNode + TemplateNodeEvaluator

Ist es template-lokal deklariert?
  -> DeclareInstruction + RuntimeTypeSpecification + InvokeInstruction

Ist es Konfiguration?
  -> modellierte Defaults + ggf. transient JVM cache
```

Die Architektur steht und fällt damit, dass diese Ebenen nicht vermischt werden. Genau darin liegt der Gewinn gegenüber klassischen Template-Engines: Die Sprache ist nur eine Projektion auf ein reflektives, validierbares, transportierbares Modell.

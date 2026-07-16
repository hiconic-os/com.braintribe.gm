# Modeled Templates

Ein reflektiertes, completion-getriebenes Templatesystem auf Basis des Hiconic/GM-Exo-Typsystems. Syntax wird nicht in einen separaten, untypisierten AST übersetzt, sondern erzeugt modellierte Entities, ValueDescriptoren und TemplateNodes. Reflection, Metadata und Experten liefern Parsing, Completion, Validierung und Evaluation.

## Wiedereinstieg

In dieser Reihenfolge lesen:

1. [modeled-template-handover.md](modeled-template-handover.md) — kompakter Gesamtstand und Wiederaufnahme.
2. [hiconic-template-system.md](hiconic-template-system.md) — vollständige Architektur- und Sprachreferenz.
3. [modeled-template-open-issues.md](modeled-template-open-issues.md) — ausschließlich echte offene Arbeiten, priorisiert.

Die ausführbare Wahrheit liegt zusätzlich in:

- `modeled-templates-model` — AST, Directives, VDs, Pfade, Reasons und Konfiguration.
- `modeled-templates` — Parser, Completion, Experten, Evaluation und Factory-API.
- `modeled-templates-test-model` — Modelltypen für Integrations- und Metadata-Tests.
- `modeled-templates-test` — Ende-zu-Ende- und Unit-Tests.
- `modeled-templates/vscode-modeled-templates` — TextMate-Grammatik für reine und eingebettete MT-Dateien.

## Syntax-Kurzfassung

`%(` eröffnet eine Template-Control-Form. Die meisten `%()`-Formen erzeugen über Reflection modellierte `DirectiveNode`s, zum Beispiel `if`, `for-each`, `switch`, `var`, `declare-instruction` oder ein declared-instruction-Invoke.

Blockmarker sind die wichtige Ausnahme:

- `%(else)`, `%(case ...)`, `%(default)` und ähnliche secondary markers trennen benannte Blöcke. Sie werden nicht als frei stehende Runtime-Statements evaluiert, sondern beim Block-Wiring in modellierte Properties des umgebenden Block-Owners übertragen, bei `switch` etwa in `SwitchCase`-Entities.
- `%(end)` ist ein reiner struktureller Terminator. Er erzeugt keine Entity und keinen `TemplateNode`, sondern beendet den aktuell geparsten Block.

Damit bleibt die Template-Syntax stromartig und HTML-nah. Der Blockinhalt muss nicht als `block: { ... }` in ein Entity-Literal gezwängt werden, während die semantischen Teile weiterhin sauber als GM-Modellgraph entstehen.

## Whitespace-Kurzfassung

Whitespace ist modellierte Directive-Semantik, kein spätes Pretty-Printing:

- `StatementInstructionNode`s wie `var`, `set` und Collection-Mutationen sind `SilentNode`s. Ohne explizite `whitespace:`-Policy entfernen sie eine reine Statement-Zeile vollständig (`trimLine` vor/nach dem Node).
- `BlockInstructionNode`s wie `if`, `for-each` und `switch` entfernen die syntaktische Opening-Zeile vor dem Node. Zusätzlich wird die Einrückung der Opening-Zeile aus den Body-Zeilen entfernt. Dadurch darf der Body PL-artig eine Ebene tiefer stehen, ohne zusätzlichen fachlichen Output-Whitespace zu erzeugen.
- Der Whitespace nach dem Opening-Marker bleibt im Block, aber bereits relativ zur Opening-Zeile normalisiert. Dadurch emittiert ein leerer Block keine syntaktische Leerzeile, ein nicht-leerer Block aber seine natürliche erste Output-Zeile.
- Die Whitespace-Zeile unmittelbar vor `%(end)`, `%(else)`, `%(case)` usw. wird beim Block-Wiring aus dem jeweiligen `TemplateNode`-Block entfernt. Das ist AST-Semantik, keine Render-Nachbearbeitung.
- Inline-Whitespace bleibt erhalten; `trimLine` greift nur, wenn tatsächlich ein angrenzender Zeilenumbruch vorhanden ist. CRLF wird als ein Zeilenumbruch behandelt.
- Declared-instruction-Bodies sind relative Modellblöcke: ihre lokale Deklarations-Einrückung wird beim Completion-Schritt normalisiert.
- Beim Invoke wird die reine Space/Tab-Einrückung der Aufrufzeile streaming als Line-Prefix auf eingefügte Folgezeilen angewendet. Es wird kein gerenderter Block-String gebuffert und nachformatiert.

Beispiel:

```mt
%(declare-instruction node {person TestPerson})
	<div>
		<label>${person.name}</label>
	</div>
%(end)

<body>
	%(node input)
</body>
```

ergibt:

```html
<body>
	<div>
		<label>Elli</label>
	</div>
</body>
```

Die ausführliche Semantik steht in `hiconic-template-system.md`, Abschnitt `15.1 Whitespace handling`.

## Build

`hc` muss im `PATH` liegen. Nur geänderte installierbare Projekte installieren; Testprojekte werden nie installiert.

```text
cd modeled-templates-model && hc install
cd ../modeled-templates && hc install
cd ../modeled-templates-test && hc compile && hc test
```

Zuletzt validierter Stand: 80 erfolgreiche Tests. Ein weiterer Recovery-Regressionsfall ist ergänzt und lokal mit `hc test` zu verifizieren.

## Transferhinweis

Ein frischer GitHub-Checkout enthält nur commitete und gepushte Dateien. Vor einem Rechnerwechsel deshalb zwingend `git status`, Commit und Push prüfen. Build-Artefakte und installierte lokale Artefakte ersetzen keinen Git-Transfer.

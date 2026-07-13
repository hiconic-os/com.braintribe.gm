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

## Build

`hc` muss im `PATH` liegen. Nur geänderte installierbare Projekte installieren; Testprojekte werden nie installiert.

```text
cd modeled-templates-model && hc install
cd ../modeled-templates && hc install
cd ../modeled-templates-test && hc compile && hc test
```

Zuletzt validierter Stand: 77 erfolgreiche Tests.

## Transferhinweis

Ein frischer GitHub-Checkout enthält nur commitete und gepushte Dateien. Vor einem Rechnerwechsel deshalb zwingend `git status`, Commit und Push prüfen. Build-Artefakte und installierte lokale Artefakte ersetzen keinen Git-Transfer.

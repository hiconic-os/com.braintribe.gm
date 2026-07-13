# hiconic Modeled Templates for VS Code

TextMate syntax highlighting for modeled templates. The extension is declarative and has no runtime code.

## Host-aware file names

Use the host language before the `.mt` suffix:

| File name | Host grammar |
| --- | --- |
| `demo.html.mt` | HTML |
| `demo.json.mt` | JSON |
| `demo.yaml.mt` / `demo.yml.mt` | YAML |
| `demo.xml.mt` | XML |
| `demo.md.mt` | Markdown |
| `demo.css.mt` | CSS |
| `demo.js.mt` | JavaScript |
| `demo.ts.mt` | TypeScript |
| `demo.mt` | Plain modeled template |

The host grammar remains active and modeled-template constructs are injected with left priority. This also highlights MT expressions inside host strings and attributes.

## Local demo

Open this directory in VS Code and press `F5`, or install the generated VSIX. After installation, reload the existing VS Code window. The language mode shown in the status bar should be `Modeled Template HTML`, `Modeled Template JSON`, and so on.

The grammar recognizes `%()` directives, `${}` outputs, `#{}` comments, explicit and inferred entities, named arguments, collection type decorators, enum literals, paths, optional access, number suffixes and the general `|` operator.

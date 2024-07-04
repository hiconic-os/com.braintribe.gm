// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.meta.data.display.formatting;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

/**
 * corresponds to the Text Code Formats (Java, Groovy, JSON, XML, ...) 
 * 
 */

public enum CodeFormat implements EnumBase {
	/** ABAP (Advanced Business Application Programming). */
	abap,
	/** Actionscript. */
	actionscript,
	/** Ada. */
	ada,
	/** Apache configuration. */
	apache_conf,
	/** Applescript. */
	applescript,
	/** ASCIIDOC. */
	asciidoc,
	/** Assembly (x86). */
	assembly_x86,
	/** Auto Hotkey. */
	autohotkey,
	/** Batch file. */
	batchfile,
	/** c9search */
	c9search,
	/** C/C++. */
	c_cpp,
	/** Cirru, indentation-based grammar for languages. */
	cirru,
	/** Clojure. */
	clojure,
	/** COBOL. */
	cobol,
	/** Coffee. */
	coffee,
	/** ColdFusion. */
	coldfusion,
	/** C#. */
	csharp,
	/** CSS. */
	css,
	/* Curly. */
	curly,
	/** Dart. */
	Dart,
	/** Diff. */
	diff,
	/** Django. */
	django,
	/** D. */
	d,
	/** Docker files. */
	dockerfile,
	/** Dot. */
	dot,
	/** EJS (Embedded Javascript). */
	ejs,
	/** Erlang. */
	erlang,
	/** Forth. */
	forth,
	/** FTL. */
	ftl,
	/** Gherkin: Business Readable, Domain Specific Language. */
	gherkin,
	/** GLSL (OpenGL Shading Language). */
	glsl,
	/** Go (http://golang.org/). */
	golang,
	/** Groovy. */
	groovy,
	/** HAML. */
	haml,
	/** Handlebars.js: Minimal Templating on Steroids. */
	handlebars,
	/** Haskell. */
	haskell,
	/** Haxe. */
	haxe,
	/** HTML. */
	html,
	/** HTML completions. */
	html_completions,
	/** HTML (Ruby). */
	html_ruby,
	/** Ini file. */
	ini,
	/** JADE. */
	jade,
	/** Jack. */
	jack,
	/** JAVA. */
	java,
	/** Javascript. */
	javascript,
	/** JSONiq, the JSON Query Language. */
	jsoniq,
	/** JSON. */
	json,
	/** JSP, Java Server Pages. */
	jsp,
	/** JSX. */
	jsx,
	/** Julia. */
	julia,
	/** LaTeX. */
	latex,
	/** Less. */
	less,
	/** Liquid. */
	liquid,
	/** LISP. */
	lisp,
	/** Livescript. */
	livescript,
	/** LogiQL. */
	logiql,
	/** LSL. */
	lsl,
	/** Lua. */
	lua,
	/** Luapage. */
	luapage,
	/** Lucene. */
	lucene,
	/** Makefile. */
	makefile,
	/** Markdown. */
	markdown,
	/** Matlab. */
	matlab,
	/** Mel. */
	mel,
	/** MUSHCode (High Rules). */
	mushcode_high_rules,
	/** MUSHCode. */
	mushcode,
	/** MySQL. */
	mysql,
	/** Nix. */
	nix,
	/** Objective C. */
	objectivec,
	/** OCaml. */
	ocaml,
	/** Pascal. */
	pascal,
	/** Perl. */
	perl,
	/** PgSQL. */
	pgsql,
	/** PHP. */
	php,
	/** Plain text. */
	plain_text,
	/** PowerShell. */
	powershell,
	/** Prolog. */
	prolog,
	/** Java properties file. */
	properties,
	/** Protocol Buffers - Google's data interchange format. */
	protobuf,
	/** Python. */
	python,
	/** RDoc (Ruby documentation). */
	rdoc,
	/** RHTML. */
	rhtml,
	/** R. */
	r,
	/** Ruby. */
	ruby,
	/** Rust. */
	rust,
	/** SASS. */
	sass,
	/** Scad. */
	scad,
	/** Scala. */
	scala,
	/** Scheme. */
	scheme,
	/** SCSS. */
	scss,
	/** Sh (Bourne shell). */
	sh,
	/** Sjs. */
	sjs,
	/** Smarty (PHP template engine). */
	smarty,
	/** Snippets. */
	snippets,
	/** Soy template. */
	soy_template,
	/** Space. */
	space,
	/** SQL. */
	sql,
	/** Stylus. */
	stylus,
	/** SVG. */
	svg,
	/** Tcl. */
	tcl,
	/** TeX. */
	tex,
	/** Text. */
	text,
	/** Textile. */
	textile,
	/** TOML. */
	toml,
	/** TWIG. */
	twig,
	/** TypeScript. */
	typescript,
	/** Vala. */
	vala,
	/** VBScript. */
	vbscript,
	/** Velocity. */
	velocity,
	/** Verilog. */
	verilog,
	/** VHDL. */
	vhdl,
	/** XML. */
	xml,
	/** XQuery. */
	xquery,
	/** YAML. */
	yaml;

	public static final EnumType<CodeFormat> T = EnumTypes.T(CodeFormat.class);
	
	@Override
	public EnumType<CodeFormat> type() {
		return T;
	}
}

package dev.hiconic.template.impl.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.reflection.Property;

import dev.hiconic.template.api.ParseRecoveryMode;
import dev.hiconic.template.api.TemplateParser;
import dev.hiconic.template.api.TemplateParserOptions;
import dev.hiconic.template.api.TemplateParserResolver;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.CommentNode;
import dev.hiconic.template.model.core.ErrorNode;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.SequenceNode;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.TextNode;
import dev.hiconic.template.model.core.instr.BlockInstructionNode;
import dev.hiconic.template.model.core.instr.Switch;
import dev.hiconic.template.model.core.instr.SwitchCase;
import dev.hiconic.template.model.parse.TemplateParseError;
import dev.hiconic.template.model.parse.TextPosition;
import dev.hiconic.template.model.parse.TextRange;

public class StandardTemplateParser implements TemplateParser {
	private final TemplateParserResolver resolver;
	private final ValidationContext validationContext;
	private final TemplateParserOptions options;

	public StandardTemplateParser(TemplateParserResolver resolver, ValidationContext validationContext, TemplateParserOptions options) {
		this.resolver = Objects.requireNonNull(resolver, "resolver");
		this.validationContext = Objects.requireNonNull(validationContext, "validationContext");
		this.options = Objects.requireNonNull(options, "options");
	}

	@Override
	public Maybe<TemplateNode> parse(String source) {
		State state = new State(Objects.requireNonNull(source, "source"));
		Chunk rootChunk = state.parseSequence(Set.of());
		SequenceNode rootSequence = rootChunk.sequence();

		if (rootChunk.marker() != null)
			state.addError("Unexpected block marker '" + rootChunk.marker().name() + "'", rootChunk.marker().range(), null);

		Reason wiringError = resolver.completeAndValidate(validationContext, rootSequence, state.range(0, source.length()));
		if (wiringError != null) {
			state.addError("Template wiring or completion failed", state.range(0, source.length()), wiringError);
			if (options.recoveryMode() != ParseRecoveryMode.STRICT)
				rootSequence.getNodes().add(state.errorNode("Template wiring or completion failed", state.range(0, source.length())));
		}

		TemplateNode root = state.compact(rootSequence);
		if (state.errors.isEmpty())
			return Maybe.complete(root);

		ParseError aggregate = ParseError.create("Template contains " + state.errors.size() + " error(s)");
		aggregate.getReasons().addAll(state.errors);

		if (options.recoveryMode() == ParseRecoveryMode.STRICT)
			return Maybe.empty(aggregate);

		return Maybe.incomplete(root, aggregate);
	}

	private final class State {
		private final String source;
		private final int[] lines;
		private final int[] columns;
		private final List<Reason> errors = new ArrayList<>();
		private int offset;

		private State(String source) {
			this.source = source;
			this.lines = new int[source.length() + 1];
			this.columns = new int[source.length() + 1];
			indexPositions();
		}

		private Chunk parseSequence(Set<String> stopMarkers) {
			List<TemplateNode> nodes = new ArrayList<>();
			int sequenceStart = offset;

			while (offset < source.length()) {
				int markerStart = findNextMarker(offset);
				if (markerStart < 0) {
					addText(nodes, offset, source.length());
					offset = source.length();
					break;
				}

				addText(nodes, offset, markerStart);
				char sigil = source.charAt(markerStart);
				int close = findMarkerEnd(markerStart + 2);
				if (close < 0) {
					TextRange range = range(markerStart, source.length());
					nodes.add(recover("Unterminated '" + sigil + "{' construct", range, null));
					offset = source.length();
					break;
				}

				boolean blockFree = sigil == '%' && close + 1 < source.length() && source.charAt(close + 1) == '%'
						&& (close + 2 >= source.length() || source.charAt(close + 2) != '{');
				int constructEnd = close + (blockFree ? 2 : 1);
				String content = source.substring(markerStart + 2, close).trim();
				TextRange constructRange = range(markerStart, constructEnd);
				offset = constructEnd;

				if (sigil == '%' || sigil == '#') {
					String markerName = firstWord(content);
					if (stopMarkers.contains(markerName))
						return chunk(nodes, sequenceStart, new Marker(markerName, content, constructRange));

					if (isMarker(content)) {
						nodes.add(recover("Unexpected block marker '" + markerName + "'", constructRange, null));
						continue;
					}
				}

				if (sigil == '$') {
					addResolved(nodes, resolver.resolveOutput(content, constructRange), constructRange, "output");
					continue;
				}

				if (sigil == '#' && !"declare-instruction".equals(firstWord(content))) {
					CommentNode comment = CommentNode.T.create();
					comment.setText(content);
					nodes.add(comment);
					continue;
				}

				Maybe<? extends TemplateNode> resolved = resolver.resolveDirective(sigil, content, blockFree, constructRange);
				TemplateNode node = valueOrError(resolved, constructRange, "directive");
				if (node == null) {
					if (sigil == '%' && !blockFree) {
						Chunk skippedBlock = parseSequence(Set.of("end"));
						if (skippedBlock.marker() == null)
							addError("Missing block end after invalid directive", constructRange, null);
					}
					nodes.add(errorNode("Could not resolve directive", constructRange));
					continue;
				}

				boolean blockInstruction = isBlockInstruction(node);
				if (blockInstruction && !blockFree) {
					enterBlock(node, "block", constructRange);
					Chunk body;
					try {
						body = parseSequence(blockMarkers(node));
					} finally {
						resolver.exitBlock(node, "block");
					}
					BlockInstructionNode.block.property().setDirect(node, compact(body.sequence()));

					Marker marker = body.marker();
					if (marker == null) {
						addError("Missing '%{end}' for block", constructRange, null);
					}
					while (marker != null && !"end".equals(marker.name())) {
						Marker secondaryMarker = marker;
						enterBlock(node, marker.name(), marker.range());
						Chunk secondary;
						try {
							secondary = parseSequence(blockMarkers(node));
						} finally {
							resolver.exitBlock(node, marker.name());
						}
						if (!wireSecondaryBlock(node, marker, compact(secondary.sequence())))
							nodes.add(errorNode("Could not wire secondary block '" + marker.name() + "'", marker.range()));
						marker = secondary.marker();
						if (marker == null)
							addError("Missing '%{end}' after '%{" + secondaryMarker.name() + "}'",
									secondaryMarker.range(), null);
					}
				} else if (blockInstruction && blockFree) {
					nodes.add(recover("Block instruction must not use block-free '%{...}%'' syntax", constructRange, null));
					continue;
				}

				Reason completionError = resolver.completeAndValidate(validationContext, node, constructRange);
				if (completionError == null)
					nodes.add(node);
				else
					nodes.add(recover("Completion or validation failed for '" + firstWord(content) + "'", constructRange, completionError));
			}

			return chunk(nodes, sequenceStart, null);
		}

		private void enterBlock(TemplateNode owner, String property, TextRange range) {
			Reason reason = resolver.enterBlock(owner, property, range);
			if (reason != null)
				addError("Could not establish scope for block '" + property + "'", range, reason);
		}

		private Set<String> blockMarkers(TemplateNode node) {
			Set<String> markers = new HashSet<>();
			markers.add("end");
			if (isSwitch(node)) {
				markers.add("case");
				markers.add("default");
			}
			for (Property property : node.entityType().getProperties()) {
				if (!"block".equals(property.getName()) && acceptsTemplateNode(property))
					markers.add(property.getName());
			}
			return markers;
		}

		private boolean isBlockInstruction(TemplateNode node) {
			return isSwitch(node)
					|| BlockInstructionNode.T.isAssignableFrom(node.entityType())
					|| node.entityType().findProperty(BlockInstructionNode.block.name()) != null;
		}

		private boolean isSwitch(TemplateNode node) {
			return Switch.T.isAssignableFrom(node.entityType())
					|| Switch.T.getTypeSignature().equals(node.entityType().getTypeSignature());
		}

		private boolean acceptsTemplateNode(Property property) {
			return property.getType().isAssignableFrom(TemplateNode.T)
					|| property.getType().getTypeSignature().equals(TemplateNode.T.getTypeSignature());
		}

		private void addResolved(List<TemplateNode> nodes, Maybe<? extends TemplateNode> maybe, TextRange range, String kind) {
			TemplateNode node = valueOrError(maybe, range, kind);
			if (node == null) {
				nodes.add(errorNode("Could not resolve " + kind, range));
				return;
			}

			Reason completionError = resolver.completeAndValidate(validationContext, node, range);
			nodes.add(completionError == null ? node : recover("Completion or validation failed for " + kind, range, completionError));
		}

		private TemplateNode valueOrError(Maybe<? extends TemplateNode> maybe, TextRange range, String kind) {
			if (maybe == null) {
				addError("Resolver returned null while resolving " + kind, range, null);
				return null;
			}
			if (maybe.isUnsatisfied()) {
				addError("Could not resolve " + kind, range, maybe.whyUnsatisfied());
				return null;
			}
			return maybe.get();
		}

		private boolean wireSecondaryBlock(TemplateNode owner, Marker marker, TemplateNode block) {
			if (isSwitch(owner))
				return wireSwitchBlock(owner, marker, block);

			Property property = owner.entityType().findProperty(marker.name());
			if (property == null) {
				addError("Instruction " + owner.entityType().getShortName()
						+ " has no secondary block property '" + marker.name() + "'", marker.range(), null);
				return false;
			}
			if (!property.getType().isAssignableFrom(block.entityType())) {
				addError("Property '" + marker.name() + "' cannot accept a TemplateNode", marker.range(), null);
				return false;
			}
			if (property.getDirect(owner) != null) {
				addError("Secondary block '" + marker.name() + "' occurs more than once", marker.range(), null);
				return false;
			}
			property.setDirect(owner, block);
			return true;
		}

		@SuppressWarnings("unchecked")
		private boolean wireSwitchBlock(TemplateNode switchNode, Marker marker, TemplateNode block) {
			List<SwitchCase> cases = Switch.cases.property().get(switchNode);
			if (cases == null) {
				cases = new ArrayList<>();
				Switch.cases.property().setDirect(switchNode, cases);
			}
			if ("default".equals(marker.name())) {
				if (Switch.defaultBlock.property().get(switchNode) != null) {
					addError("Switch default block occurs more than once", marker.range(), null);
					return false;
				}
				Switch.defaultBlock.property().setDirect(switchNode, block);
				return true;
			}
			if (!"case".equals(marker.name()))
				return false;
			String value = marker.invocation().substring(marker.name().length()).trim();
			if (value.isEmpty()) {
				addError("Switch case requires a value", marker.range(), null);
				return false;
			}
			SwitchCase switchCase = SwitchCase.T.create();
			switchCase.setValue(value);
			switchCase.setBlock(block);
			cases.add(switchCase);
			return true;
		}

		private TemplateNode recover(String message, TextRange range, Reason cause) {
			addError(message, range, cause);
			return errorNode(message, range);
		}

		private ErrorNode errorNode(String message, TextRange range) {
			ErrorNode node = ErrorNode.T.create();
			node.setMessage(message);
			node.setRange(range);
			node.setText(options.recoveryMode() == ParseRecoveryMode.SUBSTITUTE
					? options.errorPrefix() + location(range) + ": " + message + options.errorSuffix()
					: "");
			return node;
		}

		private void addError(String message, TextRange range, Reason cause) {
			TemplateParseError error = TemplateParseError.T.create();
			error.setText(message + " at " + location(range));
			error.setRange(range);
			error.setFragment(fragment(range));
			if (cause != null)
				error.causedBy(cause);
			errors.add(error);
		}

		private void addText(List<TemplateNode> nodes, int start, int end) {
			if (start == end)
				return;
			TextNode text = TextNode.T.create();
			text.setText(unescapeTemplateText(source.substring(start, end)));
			nodes.add(text);
		}

		private String unescapeTemplateText(String text) {
			StringBuilder result = null;
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				boolean escapedMarker = ch == '\\' && i + 2 < text.length()
						&& (text.charAt(i + 1) == '$' || text.charAt(i + 1) == '%' || text.charAt(i + 1) == '#')
						&& text.charAt(i + 2) == '{';
				boolean escapedBackslash = ch == '\\' && i + 1 < text.length() && text.charAt(i + 1) == '\\';
				if (!escapedMarker && !escapedBackslash) {
					if (result != null)
						result.append(ch);
					continue;
				}
				if (result == null) {
					result = new StringBuilder(text.length());
					result.append(text, 0, i);
				}
				result.append(text.charAt(++i));
			}
			return result == null ? text : result.toString();
		}

		private Chunk chunk(List<TemplateNode> nodes, int start, Marker marker) {
			SequenceNode sequence = SequenceNode.T.create();
			sequence.setNodes(nodes);
			return new Chunk(sequence, marker, range(start, offset));
		}

		private TemplateNode compact(SequenceNode sequence) {
			return sequence.getNodes().size() == 1 ? sequence.getNodes().get(0) : sequence;
		}

		private int findNextMarker(int from) {
			for (int i = from; i + 1 < source.length(); i++) {
				char ch = source.charAt(i);
				if ((ch == '$' || ch == '%' || ch == '#') && source.charAt(i + 1) == '{' && !isEscaped(i))
					return i;
			}
			return -1;
		}

		private int findMarkerEnd(int from) {
			char quote = 0;
			int nestedBraces = 0;
			int nestedBrackets = 0;
			int nestedParentheses = 0;
			for (int i = from; i < source.length(); i++) {
				char ch = source.charAt(i);
				if (quote != 0) {
					if (ch == quote && !isEscaped(i))
						quote = 0;
				} else if (isEscaped(i)) {
					continue;
				} else if (ch == '\'' || ch == '"') {
					quote = ch;
				} else if (ch == '{') {
					nestedBraces++;
				} else if (ch == '}') {
					if (nestedBraces == 0 && nestedBrackets == 0 && nestedParentheses == 0)
						return i;
					if (nestedBraces > 0)
						nestedBraces--;
				} else if (ch == '[') {
					nestedBrackets++;
				} else if (ch == ']') {
					nestedBrackets = Math.max(0, nestedBrackets - 1);
				} else if (ch == '(') {
					nestedParentheses++;
				} else if (ch == ')') {
					nestedParentheses = Math.max(0, nestedParentheses - 1);
				}
			}
			return -1;
		}

		private boolean isEscaped(int index) {
			int backslashes = 0;
			for (int i = index - 1; i >= 0 && source.charAt(i) == '\\'; i--)
				backslashes++;
			return (backslashes & 1) == 1;
		}

		private boolean isMarker(String content) {
			String word = firstWord(content);
			return "end".equals(word) || "else".equals(word) || "empty".equals(word)
					|| "default".equals(word) || "case".equals(word);
		}

		private String firstWord(String content) {
			int index = 0;
			while (index < content.length() && !Character.isWhitespace(content.charAt(index)))
				index++;
			return content.substring(0, index);
		}

		private TextRange range(int start, int end) {
			TextRange range = TextRange.T.create();
			range.setStart(position(start));
			range.setEnd(position(end));
			return range;
		}

		private TextPosition position(int at) {
			TextPosition position = TextPosition.T.create();
			position.setOffset(at);
			position.setLine(lines[at]);
			position.setColumn(columns[at]);
			return position;
		}

		private void indexPositions() {
			int line = 1;
			int column = 1;
			for (int i = 0; i <= source.length(); i++) {
				lines[i] = line;
				columns[i] = column;
				if (i < source.length()) {
					if (source.charAt(i) == '\n') {
						line++;
						column = 1;
					} else {
						column++;
					}
				}
			}
		}

		private String location(TextRange range) {
			return "line " + range.getStart().getLine() + ", column " + range.getStart().getColumn();
		}

		private String fragment(TextRange range) {
			int start = range.getStart().getOffset();
			int end = Math.min(range.getEnd().getOffset(), start + 160);
			return source.substring(start, end);
		}
	}

	private record Marker(String name, String invocation, TextRange range) {
	}

	private record Chunk(SequenceNode sequence, Marker marker, TextRange range) {
	}
}

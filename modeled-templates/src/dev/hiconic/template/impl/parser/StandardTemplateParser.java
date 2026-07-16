package dev.hiconic.template.impl.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
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
import dev.hiconic.template.model.core.SourceText;
import dev.hiconic.template.model.core.instr.BlockInstructionNode;
import dev.hiconic.template.model.core.instr.BlockClause;
import dev.hiconic.template.model.core.instr.ClauseOnlyBlockNode;
import dev.hiconic.template.model.core.instr.BlockNode;
import dev.hiconic.template.model.core.instr.DirectiveNode;
import dev.hiconic.template.model.core.instr.SilentNode;
import dev.hiconic.template.model.core.instr.InvokeInstruction;
import dev.hiconic.template.model.core.instr.InstructionNode;
import dev.hiconic.template.model.core.instr.WhitespaceAction;
import dev.hiconic.template.model.core.instr.WhitespacePolicy;
import dev.hiconic.template.model.core.instr.StatementInstructionNode;
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
		resolver.beginParse();
		try {
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
		} finally {
			resolver.endParse();
		}
	}

	private final class State {
		private final String source;
		private final int[] lines;
		private final int[] columns;
		private final List<Reason> errors = new ArrayList<>();
		private int offset;
		private WhitespaceAction pendingWhitespace = WhitespaceAction.preserve;

		private State(String source) {
			this.source = source;
			this.lines = new int[source.length() + 1];
			this.columns = new int[source.length() + 1];
			indexPositions();
		}

		private Chunk parseSequence(Set<String> stopMarkers) {
			List<TemplateNode> nodes = new ArrayList<>();
			int sequenceStart = offset;
			predeclareSequence(stopMarkers);

			while (offset < source.length()) {
				int markerStart = findNextMarker(offset);
				if (markerStart < 0) {
					addText(nodes, offset, source.length());
					offset = source.length();
					break;
				}

				addText(nodes, offset, markerStart);
				char sigil = source.charAt(markerStart);
				char closing = sigil == '%' ? ')' : '}';
				int close = findMarkerEnd(markerStart + 2, closing);
				if (close < 0) {
					TextRange range = range(markerStart, source.length());
					nodes.add(recover("Unterminated '" + sigil + (sigil == '%' ? "(" : "{") + "' construct", range, null));
					offset = source.length();
					break;
				}

				boolean blockFree = false;
				int constructEnd = close + 1;
				String rawContent = source.substring(markerStart + 2, close);
				String content = rawContent.trim();
				TextRange constructRange = range(markerStart, constructEnd);
				TextRange contentRange = range(markerStart + 2, close);
				offset = constructEnd;

				if (sigil == '%') {
					String markerName = firstWord(content);
					if (stopMarkers.contains(markerName))
						return chunk(nodes, sequenceStart, new Marker(markerName, content, constructRange));

					if (isMarker(content)) {
						nodes.add(recover("Unexpected block marker '" + markerName + "'", constructRange, null));
						continue;
					}
				}

				if (sigil == '$') {
					addResolved(nodes, resolver.resolveOutput(rawContent, contentRange), constructRange, "output");
					continue;
				}

				if (sigil == '#') {
					CommentNode comment = CommentNode.T.create();
					SourceText text = SourceText.T.create();
					text.setValue(rawContent);
					comment.setText(text);
					applyWhitespaceBefore(nodes, comment);
					nodes.add(comment);
					applyWhitespaceAfter(comment);
					continue;
				}

				Maybe<? extends TemplateNode> resolved = resolver.resolveDirective(sigil, rawContent, blockFree, contentRange);
				TemplateNode node = valueOrError(resolved, constructRange, "directive");
				if (node == null) {
					if (sigil == '%' && isKnownBlockStart(firstWord(content))) {
						Chunk skippedBlock = parseSequence(Set.of("end"));
						if (skippedBlock.marker() == null)
							addError("Missing block end after invalid directive", constructRange, null);
					}
					nodes.add(errorNode("Could not resolve directive", constructRange));
					continue;
				}

				boolean blockInstruction = isBlockInstruction(node);
				applyWhitespaceBefore(nodes, node);
				if (blockInstruction && !blockFree) {
					Reason scopeCompletion = resolver.completeScope(node, constructRange);
					if (scopeCompletion != null) addError("Could not complete instruction scope", constructRange, scopeCompletion);
					String blockIndent = lineIndentAt(markerStart);
					enterBlock(node, "block", constructRange);
					Chunk body;
					try {
						body = parseSequence(blockMarkers(node));
					} finally {
						resolver.exitBlock(node, "block");
					}
					if (node instanceof SilentNode && node instanceof DirectiveNode directive
							&& directive.getWhitespace() == null)
						trimSilentBlockBoundaries(body.sequence());
					else {
						stripBlockBodyIndent(body.sequence(), blockIndent);
						trimBlockEndBoundary(body.sequence());
					}
					TemplateNode primaryBlock = compact(body.sequence());
					if (node instanceof ClauseOnlyBlockNode && !isStructuralWhitespace(primaryBlock))
						addError(node.entityType().getShortName()
								+ " does not accept an implicit block; content must be inside clause blocks",
								constructRange, null);
					BlockNode.block.property().setDirect(node, primaryBlock);

					Marker marker = body.marker();
					if (marker == null) {
						addError("Missing '%(end)' for block", constructRange, null);
					}
					while (marker != null && !"end".equals(marker.name())) {
						Marker secondaryMarker = marker;
						String secondaryIndent = lineIndentAt(marker.range().getStart().getOffset());
						enterBlock(node, marker.name(), marker.range());
						Chunk secondary;
						try {
							secondary = parseSequence(blockMarkers(node));
						} finally {
							resolver.exitBlock(node, marker.name());
						}
						stripBlockBodyIndent(secondary.sequence(), secondaryIndent);
						trimBlockEndBoundary(secondary.sequence());
						if (!wireSecondaryBlock(node, marker, compact(secondary.sequence())))
							nodes.add(errorNode("Could not wire secondary block '" + marker.name() + "'", marker.range()));
						marker = secondary.marker();
						if (marker == null)
							addError("Missing '%(end)' after '%(" + secondaryMarker.name() + ")'",
									secondaryMarker.range(), null);
					}
				}

				Reason completionError = resolver.completeAndValidate(validationContext, node, constructRange);
				if (completionError == null)
					nodes.add(node);
				else
					nodes.add(recover("Completion or validation failed for '" + firstWord(content) + "'", constructRange, completionError));
				applyWhitespaceAfter(node);
			}

			return chunk(nodes, sequenceStart, null);
		}

		private void predeclareSequence(Set<String> stopMarkers) {
			int cursor = offset;
			int depth = 0;
			while (cursor < source.length()) {
				int markerStart = findNextMarker(cursor);
				if (markerStart < 0)
					return;
				char sigil = source.charAt(markerStart);
				char closing = sigil == '%' ? ')' : '}';
				int close = findMarkerEnd(markerStart + 2, closing);
				if (close < 0)
					return;
				boolean blockFree = false;
				int constructEnd = close + 1;
				String content = source.substring(markerStart + 2, close).trim();
				String markerName = firstWord(content);

				if (sigil == '%' && isMarker(content)) {
					if ("end".equals(markerName)) {
						if (depth == 0 && stopMarkers.contains("end"))
							return;
						if (depth > 0)
							depth--;
					} else if (depth == 0 && stopMarkers.contains(markerName)) {
						return;
					}
				} else if (depth == 0 && sigil == '%' && "declare-instruction".equals(markerName)) {
					TextRange range = range(markerStart, constructEnd);
					Reason reason = resolver.predeclareDirective(sigil, source.substring(markerStart + 2, close),
							range(markerStart + 2, close));
					if (reason != null)
						addError("Could not predeclare directive", range, reason);
					depth++;
				} else if (sigil == '%' && isKnownBlockStart(markerName)) {
					depth++;
				}
				cursor = constructEnd;
			}
		}

		private void enterBlock(TemplateNode owner, String property, TextRange range) {
			Reason reason = resolver.enterBlock(owner, property, range);
			if (reason != null)
				addError("Could not establish scope for block '" + property + "'", range, reason);
		}

		private Set<String> blockMarkers(TemplateNode node) {
			Set<String> markers = new HashSet<>();
			markers.add("end");
			markers.addAll(clauseMarkerNames(node));
			for (Property property : node.entityType().getProperties()) {
				if (!"block".equals(property.getName()) && acceptsTemplateNode(property))
					markers.add(property.getName());
			}
			return markers;
		}

		private boolean isBlockInstruction(TemplateNode node) {
			return BlockNode.T.isAssignableFrom(node.entityType())
					|| node.entityType().findProperty(BlockNode.block.name()) != null;
		}

		private Set<String> clauseMarkerNames(TemplateNode node) {
			Set<String> names = new HashSet<>();
			for (Property property : node.entityType().getProperties()) {
				GenericModelType type = clauseType(property);
				if (type != null)
					names.addAll(resolver.clauseMarkers(type));
			}
			return names;
		}

		private boolean acceptsTemplateNode(Property property) {
			return property.getType().isAssignableFrom(TemplateNode.T)
					|| property.getType().getTypeSignature().equals(TemplateNode.T.getTypeSignature());
		}

		private GenericModelType clauseType(Property property) {
			GenericModelType type = property.getType();
			if (type instanceof CollectionType collectionType)
				type = collectionType.getCollectionElementType();
			return type instanceof EntityType<?> entityType && BlockClause.T.isAssignableFrom(entityType)
					? entityType : null;
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

		private <T> T valueOrError(Maybe<? extends T> maybe, TextRange range, String kind) {
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
			if (wireClauseBlock(owner, marker, block))
				return true;

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
		private boolean wireClauseBlock(TemplateNode owner, Marker marker, TemplateNode block) {
			ClauseSlot slot = resolveClauseSlot(owner, marker.name());
			if (slot == null)
				return false;
			String arguments = marker.invocation().substring(marker.name().length()).trim();
			Maybe<? extends GenericEntity> maybe = resolver.resolveClause(slot.type(), marker.name(), arguments, marker.range());
			GenericEntity entity = valueOrError(maybe, marker.range(), "clause");
			if (!(entity instanceof BlockClause clause)) {
				addError("Clause marker '" + marker.name() + "' did not resolve to a BlockClause", marker.range(), null);
				return false;
			}
			clause.setBlock(block);
			if (slot.collection()) {
				List<BlockClause> clauses = (List<BlockClause>) slot.property().get(owner);
				if (clauses == null) {
					clauses = new ArrayList<>();
					slot.property().setDirect(owner, clauses);
				}
				clauses.add(clause);
			} else {
				if (slot.property().getDirect(owner) != null) {
					addError("Clause '" + marker.name() + "' occurs more than once", marker.range(), null);
					return false;
				}
				slot.property().setDirect(owner, clause);
			}
			return true;
		}

		private ClauseSlot resolveClauseSlot(TemplateNode owner, String markerName) {
			for (Property property : owner.entityType().getProperties()) {
				GenericModelType type = property.getType();
				boolean collection = false;
				if (type instanceof CollectionType collectionType) {
					collection = true;
					type = collectionType.getCollectionElementType();
				}
				if (!(type instanceof EntityType<?> entityType) || !BlockClause.T.isAssignableFrom(entityType))
					continue;
				if (resolver.clauseMarkers(entityType).contains(markerName))
					return new ClauseSlot(property, entityType, collection);
			}
			return null;
		}

		private record ClauseSlot(Property property, GenericModelType type, boolean collection) {
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
			String value = applyLeadingWhitespace(source.substring(start, end), pendingWhitespace);
			pendingWhitespace = WhitespaceAction.preserve;
			if (value.isEmpty())
				return;
			TextNode text = TextNode.T.create();
			text.setText(unescapeTemplateText(value));
			nodes.add(text);
		}

		private void applyWhitespaceBefore(List<TemplateNode> nodes, TemplateNode node) {
			WhitespaceAction action = whitespace(node, true);
			if (action == WhitespaceAction.preserve || nodes.isEmpty())
				return;
			TemplateNode previous = nodes.get(nodes.size() - 1);
			if (!(previous instanceof TextNode text))
				return;
			String trimmed = applyTrailingWhitespace(text.getText(), action);
			if (trimmed.isEmpty()) nodes.remove(nodes.size() - 1); else text.setText(trimmed);
		}

		private void applyWhitespaceAfter(TemplateNode node) {
			pendingWhitespace = whitespace(node, false);
		}

		private void trimSilentBlockBoundaries(SequenceNode sequence) {
			List<TemplateNode> nodes = sequence.getNodes();
			if (nodes.isEmpty()) return;
			if (nodes.get(0) instanceof TextNode first) {
				String value = applyLeadingWhitespace(first.getText(), WhitespaceAction.trimLine);
				if (value.isEmpty()) nodes.remove(0); else first.setText(value);
			}
			if (!nodes.isEmpty() && nodes.get(nodes.size() - 1) instanceof TextNode last) {
				String value = applyTrailingWhitespace(last.getText(), WhitespaceAction.trimLine);
				if (value.isEmpty()) nodes.remove(nodes.size() - 1); else last.setText(value);
			}
		}

		private void trimBlockEndBoundary(SequenceNode sequence) {
			List<TemplateNode> nodes = sequence.getNodes();
			if (nodes.isEmpty()) return;
			if (nodes.get(nodes.size() - 1) instanceof TextNode last) {
				String value = applyTrailingWhitespace(last.getText(), WhitespaceAction.trimLine);
				if (value.isEmpty()) nodes.remove(nodes.size() - 1); else last.setText(value);
			}
		}

		private void stripBlockBodyIndent(SequenceNode sequence, String indent) {
			if (indent == null || indent.isEmpty())
				return;
			for (TemplateNode node : sequence.getNodes()) {
				if (node instanceof TextNode text)
					text.setText(stripLineIndent(text.getText(), indent));
			}
		}

		private String stripLineIndent(String text, String indent) {
			StringBuilder result = null;
			boolean lineStart = true;
			int i = 0;
			while (i < text.length()) {
				if (lineStart && startsWith(text, i, indent)) {
					if (result == null) {
						result = new StringBuilder(text.length());
						result.append(text, 0, i);
					}
					i += indent.length();
					lineStart = false;
					continue;
				}
				char ch = text.charAt(i++);
				if (result != null)
					result.append(ch);
				lineStart = ch == '\n';
			}
			return result == null ? text : result.toString();
		}

		private boolean startsWith(String text, int index, String prefix) {
			if (index + prefix.length() > text.length())
				return false;
			for (int i = 0; i < prefix.length(); i++) {
				if (text.charAt(index + i) != prefix.charAt(i))
					return false;
			}
			return true;
		}

		private WhitespaceAction whitespace(TemplateNode node, boolean before) {
			if (!(node instanceof DirectiveNode directive))
				return node instanceof SilentNode ? WhitespaceAction.trimLine : WhitespaceAction.preserve;
			WhitespacePolicy policy = directive.getWhitespace();
			if (policy == null && node instanceof InvokeInstruction)
				return WhitespaceAction.preserve;
			if (policy == null && node instanceof BlockInstructionNode)
				return before ? WhitespaceAction.trimLine : WhitespaceAction.preserve;
			if (policy == null)
				return WhitespaceAction.trimLine;
			WhitespaceAction action = before ? policy.getBefore() : policy.getAfter();
			return action == null ? WhitespaceAction.preserve : action;
		}

		private String applyLeadingWhitespace(String text, WhitespaceAction action) {
			if (action == WhitespaceAction.preserve) return text;
			int i = 0;
			if (action == WhitespaceAction.trim) {
				while (i < text.length() && Character.isWhitespace(text.charAt(i))) i++;
			} else {
				while (i < text.length() && (text.charAt(i) == ' ' || text.charAt(i) == '\t' || text.charAt(i) == '\r')) i++;
				if (i < text.length() && text.charAt(i) == '\n') i++; else return text;
			}
			return text.substring(i);
		}

		private String applyTrailingWhitespace(String text, WhitespaceAction action) {
			int i = text.length();
			if (action == WhitespaceAction.trim) {
				while (i > 0 && Character.isWhitespace(text.charAt(i - 1))) i--;
			} else {
				while (i > 0 && (text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\t' || text.charAt(i - 1) == '\r')) i--;
				if (i > 0 && text.charAt(i - 1) == '\n') {
					i--;
					if (i > 0 && text.charAt(i - 1) == '\r') i--;
				} else {
					return text;
				}
			}
			return text.substring(0, i);
		}

		private String lineIndentAt(int offset) {
			int lineStart = offset;
			while (lineStart > 0) {
				char ch = source.charAt(lineStart - 1);
				if (ch == '\n' || ch == '\r')
					break;
				lineStart--;
			}
			for (int i = lineStart; i < offset; i++) {
				char ch = source.charAt(i);
				if (ch != ' ' && ch != '\t')
					return "";
			}
			return source.substring(lineStart, offset);
		}

		private String unescapeTemplateText(String text) {
			StringBuilder result = null;
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				boolean escapedMarker = ch == '\\' && i + 2 < text.length()
						&& ((text.charAt(i + 1) == '$' || text.charAt(i + 1) == '#') && text.charAt(i + 2) == '{'
								|| text.charAt(i + 1) == '%' && text.charAt(i + 2) == '(');
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

		private boolean isStructuralWhitespace(TemplateNode node) {
			if (node instanceof CommentNode)
				return true;
			if (node instanceof TextNode text)
				return text.getText() == null || text.getText().isBlank();
			if (node instanceof SequenceNode sequence) {
				if (sequence.getNodes() == null) return true;
				for (TemplateNode child : sequence.getNodes())
					if (!isStructuralWhitespace(child))
						return false;
				return true;
			}
			return false;
		}

		private int findNextMarker(int from) {
			for (int i = from; i + 1 < source.length(); i++) {
				char ch = source.charAt(i);
				if (((ch == '$' || ch == '#') && source.charAt(i + 1) == '{'
						|| ch == '%' && source.charAt(i + 1) == '(') && !isEscaped(i))
					return i;
			}
			return -1;
		}

		private int findMarkerEnd(int from, char closing) {
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
					if (closing == '}' && nestedBraces == 0 && nestedBrackets == 0 && nestedParentheses == 0)
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
					if (closing == ')' && nestedParentheses == 0 && nestedBraces == 0 && nestedBrackets == 0)
						return i;
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
					|| "default".equals(word) || "case".equals(word) || "when".equals(word);
		}

		private boolean isKnownBlockStart(String name) {
			return "if".equals(name) || "for-each".equals(name) || "for-each-entry".equals(name) || "switch".equals(name)
					|| "while".equals(name) || "repeat".equals(name) || "declare-instruction".equals(name);
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

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
import dev.hiconic.template.api.TemplateSource;
import dev.hiconic.template.api.TemplateSource.MarkerToken;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.CommentNode;
import dev.hiconic.template.model.core.ErrorNode;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.SequenceNode;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.TextNode;
import dev.hiconic.template.model.core.SourceText;
import dev.hiconic.template.model.core.instr.BlockClause;
import dev.hiconic.template.model.core.instr.ClauseOnlyBlockNode;
import dev.hiconic.template.model.core.instr.BlockNode;
import dev.hiconic.template.model.core.instr.DirectiveNode;
import dev.hiconic.template.model.core.instr.SilentNode;
import dev.hiconic.template.model.core.instr.InstructionNode;
import dev.hiconic.template.model.core.instr.StatementInstructionNode;
import dev.hiconic.template.model.parse.TemplateParseError;
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
		return parse(new StringTemplateSource(Objects.requireNonNull(source, "source")));
	}

	/**
	 * Parses the given {@link TemplateSource}. The string entry point wraps its argument into a
	 * {@link StringTemplateSource}; a document-oriented front-end supplies its own implementation.
	 * The block/scope/predeclaration/wiring logic below is identical regardless of the source.
	 */
	public Maybe<TemplateNode> parse(TemplateSource source) {
		resolver.beginParse();
		try {
			State state = new State(Objects.requireNonNull(source, "source"));
			Chunk rootChunk = state.parseSequence(Set.of());
			SequenceNode rootSequence = rootChunk.sequence();

			if (rootChunk.marker() != null)
				state.addError("Unexpected block marker '" + rootChunk.marker().name() + "'", rootChunk.marker().range(), null);

			Reason wiringError = resolver.completeAndValidate(validationContext, rootSequence, state.range(0, state.sourceLength()));
			if (wiringError != null) {
				state.addError("Template wiring or completion failed", state.range(0, state.sourceLength()), wiringError);
				if (options.recoveryMode() != ParseRecoveryMode.STRICT)
					rootSequence.getNodes().add(state.errorNode("Template wiring or completion failed", state.range(0, state.sourceLength())));
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
		private final TemplateSource source;
		private final List<Reason> errors = new ArrayList<>();
		private int offset;

		private State(TemplateSource source) {
			this.source = source;
		}

		private int sourceLength() {
			return source.length();
		}

		private Chunk parseSequence(Set<String> stopMarkers) {
			List<TemplateNode> nodes = new ArrayList<>();
			int sequenceStart = offset;
			predeclareSequence(stopMarkers);

			while (offset < source.length()) {
				int markerStart = source.findNextMarker(offset);
				if (markerStart < 0) {
					source.materializeStatic(nodes, offset, source.length());
					offset = source.length();
					break;
				}

				source.materializeStatic(nodes, offset, markerStart);
				MarkerToken token = source.scanMarker(markerStart);
				if (!token.terminated()) {
					char sigil = token.sigil();
					nodes.add(recover("Unterminated '" + sigil + (sigil == '%' ? "(" : "{") + "' construct",
							token.constructRange(), null));
					offset = source.length();
					break;
				}

				boolean blockFree = false;
				char sigil = token.sigil();
				int constructEnd = token.constructEnd();
				String rawContent = token.rawContent();
				String content = rawContent.trim();
				TextRange constructRange = token.constructRange();
				TextRange contentRange = token.contentRange();
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
					source.applyWhitespaceBefore(nodes, comment);
					nodes.add(comment);
					source.applyWhitespaceAfter(comment);
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
				source.applyWhitespaceBefore(nodes, node);
				if (blockInstruction && !blockFree) {
					Reason scopeCompletion = resolver.completeScope(node, constructRange);
					if (scopeCompletion != null) addError("Could not complete instruction scope", constructRange, scopeCompletion);
					String blockIndent = source.lineIndentAt(markerStart);
					enterBlock(node, "block", constructRange);
					Chunk body;
					try {
						body = parseSequence(blockMarkers(node));
					} finally {
						resolver.exitBlock(node, "block");
					}
					if (node instanceof SilentNode && node instanceof DirectiveNode directive
							&& directive.getWhitespace() == null)
						source.trimSilentBlockBoundaries(body.sequence());
					else {
						source.stripBlockBodyIndent(body.sequence(), blockIndent);
						source.trimBlockEndBoundary(body.sequence());
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
						String secondaryIndent = source.lineIndentAt(marker.range().getStart().getOffset());
						enterBlock(node, marker.name(), marker.range());
						Chunk secondary;
						try {
							secondary = parseSequence(blockMarkers(node));
						} finally {
							resolver.exitBlock(node, marker.name());
						}
						source.stripBlockBodyIndent(secondary.sequence(), secondaryIndent);
						source.trimBlockEndBoundary(secondary.sequence());
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
				source.applyWhitespaceAfter(node);
			}

			return chunk(nodes, sequenceStart, null);
		}

		private void predeclareSequence(Set<String> stopMarkers) {
			int cursor = offset;
			int depth = 0;
			while (cursor < source.length()) {
				int markerStart = source.findNextMarker(cursor);
				if (markerStart < 0)
					return;
				MarkerToken token = source.scanMarker(markerStart);
				if (!token.terminated())
					return;
				char sigil = token.sigil();
				int constructEnd = token.constructEnd();
				String content = token.rawContent().trim();
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
					Reason reason = resolver.predeclareDirective(sigil, token.rawContent(), token.contentRange());
					if (reason != null)
						addError("Could not predeclare directive", token.constructRange(), reason);
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
			return source.range(start, end);
		}

		private String location(TextRange range) {
			return source.location(range);
		}

		private String fragment(TextRange range) {
			return source.fragment(range);
		}
	}

	private record Marker(String name, String invocation, TextRange range) {
	}

	private record Chunk(SequenceNode sequence, Marker marker, TextRange range) {
	}
}

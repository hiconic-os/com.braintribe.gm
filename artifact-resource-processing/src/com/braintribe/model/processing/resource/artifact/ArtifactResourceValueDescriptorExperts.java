// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.resource.artifact;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.processing.resource.artifact.api.ArtifactResourceResolver;
import com.braintribe.model.processing.vde.expression.ModelBasedValueDescriptorExpressionCodec;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionCodec;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionProjection;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorSourceContext;
import com.braintribe.model.processing.vde.reasoned.impl.ValueDescriptorExpertRegistry;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.ClassPathSource;
import com.braintribe.model.resource.source.vd.ArtifactResource;

/** Generic expression vocabulary, evaluation and lossless projection for artifact-relative resources. */
public final class ArtifactResourceValueDescriptorExperts {

	private ArtifactResourceValueDescriptorExperts() {
	}

	public static ValueDescriptorExpressionCodec expressionCodec() {
		return new ModelBasedValueDescriptorExpressionCodec(
				com.braintribe.model.resource.source.vd.ArtifactResourceSource.T, ArtifactResource.T);
	}

	public static void register(ValueDescriptorExpertRegistry registry, ArtifactResourceResolver resolver) {
		registry.register(com.braintribe.model.resource.source.vd.ArtifactResourceSource.T, (context, descriptor) -> {
			Maybe<ResolvedPath> resolved = resolve(context.getAspect(ValueDescriptorSourceContext.class),
					descriptor.getArtifact(), descriptor.getPath());
			if (resolved.isUnsatisfied())
				return resolved.whyUnsatisfied().asMaybe();
			ResolvedPath path = resolved.get();
			return resolver.resolveSource(path.artifact, path.path);
		});

		registry.register(ArtifactResource.T, (context, descriptor) -> {
			Maybe<ResolvedPath> resolved = resolve(context.getAspect(ValueDescriptorSourceContext.class),
					descriptor.getArtifact(), descriptor.getPath());
			if (resolved.isUnsatisfied())
				return resolved.whyUnsatisfied().asMaybe();
			ResolvedPath path = resolved.get();
			return resolver.resolveResource(path.artifact, path.path);
		});
	}

	/** Losslessly projects existing artifact sources; surrounding Resource metadata remains explicit. */
	public static ValueDescriptorExpressionProjection projection(ValueDescriptorSourceContext target) {
		return projection(target, resource -> false, source -> false);
	}

	/**
	 * Adds two explicit export decisions: which complete Resources are regenerable and which legacy classpath paths are known to belong to the
	 * target artifact. Neither decision is inferred from mutable Resource metadata.
	 */
	public static ValueDescriptorExpressionProjection projection(ValueDescriptorSourceContext target,
			Predicate<? super Resource> regenerableResource, Predicate<? super ClassPathSource> artifactClassPathSource) {
		return (inferredType, value) -> {
			if (value instanceof Resource && regenerableResource.test((Resource) value)) {
				Resource resource = (Resource) value;
				SourceLocation location = sourceLocation(resource.getResourceSource(), target, artifactClassPathSource);
				if (location == null)
					return null;
				ArtifactResource descriptor = ArtifactResource.T.create();
				configureLocation(descriptor::setPath, descriptor::setArtifact, location, target);
				return descriptor;
			}

			SourceLocation location = sourceLocation(value, target, artifactClassPathSource);
			if (location == null)
				return null;
			com.braintribe.model.resource.source.vd.ArtifactResourceSource descriptor =
					com.braintribe.model.resource.source.vd.ArtifactResourceSource.T.create();
			configureLocation(descriptor::setPath, descriptor::setArtifact, location, target);
			return descriptor;
		};
	}

	private static SourceLocation sourceLocation(Object value, ValueDescriptorSourceContext target,
			Predicate<? super ClassPathSource> artifactClassPathSource) {
		if (value instanceof com.braintribe.model.resource.source.ArtifactResourceSource) {
			com.braintribe.model.resource.source.ArtifactResourceSource source =
					(com.braintribe.model.resource.source.ArtifactResourceSource) value;
			return valid(source.getArtifact(), source.getPath()) ? new SourceLocation(source.getArtifact(), source.getPath()) : null;
		}

		if (value instanceof ClassPathSource) {
			ClassPathSource source = (ClassPathSource) value;
			if (artifactClassPathSource.test(source) && source.getPath() != null && !source.getPath().trim().isEmpty())
				return new SourceLocation(target == null ? null : target.artifact(), source.getPath());
		}

		return null;
	}

	private static boolean valid(String artifact, String path) {
		return artifact != null && !artifact.trim().isEmpty() && path != null && !path.trim().isEmpty();
	}

	private static void configureLocation(Consumer<String> pathSetter, Consumer<String> artifactSetter,
			SourceLocation source, ValueDescriptorSourceContext target) {
		if (target != null && source.artifact != null && source.artifact.equals(target.artifact())
				&& target.path() != null && !target.path().trim().isEmpty()) {
			pathSetter.accept(relativePath(target.path(), source.path));
		} else if (source.artifact == null && target != null && target.path() != null && !target.path().trim().isEmpty()) {
			pathSetter.accept(relativePath(target.path(), source.path));
		} else {
			artifactSetter.accept(source.artifact);
			pathSetter.accept(source.path);
		}
	}

	private static String relativePath(String documentPath, String resourcePath) {
		List<String> document = segments(documentPath);
		List<String> resource = segments(resourcePath);
		if (!document.isEmpty())
			document.remove(document.size() - 1);

		int common = 0;
		while (common < document.size() && common < resource.size() && document.get(common).equals(resource.get(common)))
			common++;

		List<String> result = new ArrayList<>();
		for (int i = common; i < document.size(); i++)
			result.add("..");
		result.addAll(resource.subList(common, resource.size()));
		String relative = String.join("/", result);
		return relative.startsWith("../") || relative.equals("..") ? relative : "./" + relative;
	}

	private static List<String> segments(String path) {
		List<String> result = new ArrayList<>();
		for (String segment : path.replace('\\', '/').split("/"))
			if (!segment.isEmpty() && !segment.equals("."))
				result.add(segment);
		return result;
	}

	private static Maybe<ResolvedPath> resolve(ValueDescriptorSourceContext source, String explicitArtifact, String configuredPath) {
		if (configuredPath == null || configuredPath.trim().isEmpty())
			return InvalidArgument.create("An artifact resource expression requires a non-empty path").asMaybe();

		String artifact = explicitArtifact;
		String candidate = configuredPath.replace('\\', '/');
		boolean sourceRelative = candidate.startsWith("./") || candidate.startsWith("../") || candidate.equals(".") || candidate.equals("..");
		if (artifact == null || artifact.trim().isEmpty()) {
			if (source == null || source.artifact() == null || source.artifact().trim().isEmpty())
				return InvalidArgument.create("An artifact resource has no owning artifact context: " + configuredPath).asMaybe();
			artifact = source.artifact();
			if (sourceRelative) {
				String sourcePath = source.path() == null ? "" : source.path().replace('\\', '/');
				int separator = sourcePath.lastIndexOf('/');
				candidate = (separator < 0 ? "" : sourcePath.substring(0, separator + 1)) + candidate;
			}
		} else {
			while (candidate.startsWith("./"))
				candidate = candidate.substring(2);
		}

		while (candidate.startsWith("/"))
			candidate = candidate.substring(1);
		Path normalized = Paths.get(candidate).normalize();
		String normalizedPath = normalized.toString().replace('\\', '/');
		if (normalized.isAbsolute() || normalizedPath.isEmpty() || normalizedPath.equals("..") || normalizedPath.startsWith("../"))
			return InvalidArgument.create("Artifact resource path escapes its artifact: " + configuredPath).asMaybe();

		return Maybe.complete(new ResolvedPath(artifact, normalizedPath));
	}

	private static final class SourceLocation {
		private final String artifact;
		private final String path;

		private SourceLocation(String artifact, String path) {
			this.artifact = artifact;
			this.path = path;
		}
	}

	private static final class ResolvedPath {
		private final String artifact;
		private final String path;

		private ResolvedPath(String artifact, String path) {
			this.artifact = artifact;
			this.path = path;
		}
	}
}

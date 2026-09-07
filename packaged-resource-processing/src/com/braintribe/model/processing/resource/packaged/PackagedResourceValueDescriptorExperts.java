// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.resource.packaged;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.bvd.resource.PackagedResource;
import com.braintribe.model.bvd.resource.PackagedResourceText;
import com.braintribe.model.bvd.resource.ResourceText;
import com.braintribe.model.bvd.resource.PackagedSource;
import com.braintribe.model.processing.resource.packaged.api.PackagedResourceResolver;
import com.braintribe.model.processing.vde.expression.ModelBasedValueDescriptorExpressionCodec;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionCodec;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionProjection;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorSourceContext;
import com.braintribe.model.processing.vde.reasoned.impl.ValueDescriptorExpertRegistry;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.ClassPathSource;

/**
 * Expression vocabulary, evaluation and lossless projection for files packaged in an artifact.
 * <p>
 * The vocabulary is <code>${packagedSource(path[, artifact])}</code> and <code>${packagedResource(path[, artifact])}</code>.
 * <p>
 * NOTE: {@link PackagedSource} names the value descriptor of this package, while {@link com.braintribe.model.resource.source.PackagedSource} names
 * the resource source that it resolves to. Both appear here, so the latter is written out in full.
 */
public final class PackagedResourceValueDescriptorExperts {

	private PackagedResourceValueDescriptorExperts() {
	}

	public static ValueDescriptorExpressionCodec expressionCodec() {
		return new ModelBasedValueDescriptorExpressionCodec(PackagedSource.T, PackagedResource.T, PackagedResourceText.T, ResourceText.T);
	}

	public static void register(ValueDescriptorExpertRegistry registry, PackagedResourceResolver resolver) {
		registry.register(PackagedSource.T, (context, descriptor) -> {
			Maybe<ResolvedPath> resolved = resolve(context.getAspect(ValueDescriptorSourceContext.class), descriptor.getArtifact(),
					descriptor.getPath());
			if (resolved.isUnsatisfied())
				return resolved.whyUnsatisfied().asMaybe();

			ResolvedPath path = resolved.get();
			return resolver.resolveSource(path.artifact, path.path);
		});

		registry.register(PackagedResource.T, (context, descriptor) -> {
			Maybe<ResolvedPath> resolved = resolve(context.getAspect(ValueDescriptorSourceContext.class), descriptor.getArtifact(),
					descriptor.getPath());
			if (resolved.isUnsatisfied())
				return resolved.whyUnsatisfied().asMaybe();

			ResolvedPath path = resolved.get();
			return resolver.resolveResource(path.artifact, path.path);
		});

		registry.register(PackagedResourceText.T, (context, descriptor) -> {
			Maybe<ResolvedPath> resolved = resolve(context.getAspect(ValueDescriptorSourceContext.class), descriptor.getArtifact(),
					descriptor.getPath());
			if (resolved.isUnsatisfied())
				return resolved.whyUnsatisfied().asMaybe();

			ResolvedPath path = resolved.get();
			Maybe<InputStream> in = resolver.openStream(path.artifact, path.path);
			if (in.isUnsatisfied())
				return in.whyUnsatisfied().asMaybe();

			return text(in.get(), descriptor.getEncoding(), path.toString());
		});

		registry.register(ResourceText.T, (context, descriptor) -> {
			Resource resource = descriptor.getResource();
			if (resource == null)
				return InvalidArgument.create("A resourceText expression requires a resource").asMaybe();

			Maybe<InputStream> in = openStream(resource, resolver);
			if (in.isUnsatisfied())
				return in.whyUnsatisfied().asMaybe();

			return text(in.get(), descriptor.getEncoding(), String.valueOf(resource.getName()));
		});
	}

	/**
	 * A Resource is read in the obvious way when its source can be streamed. A {@link com.braintribe.model.resource.source.PackagedSource} holds no
	 * payload, so such a Resource is read through the resolver instead.
	 */
	private static Maybe<InputStream> openStream(Resource resource, PackagedResourceResolver resolver) {
		if (resource.getResourceSource() instanceof com.braintribe.model.resource.source.PackagedSource source)
			return resolver.openStream(source.getArtifact(), source.getPath());

		if (resource.isStreamable())
			return Maybe.complete(resource.openStream());

		return InvalidArgument.create("This Resource cannot be read here, its source is not streamable: " + resource.getResourceSource()).asMaybe();
	}

	private static Maybe<String> text(InputStream in, String encoding, String what) {
		Charset charset;
		try {
			charset = encoding == null || encoding.isBlank() ? StandardCharsets.UTF_8 : Charset.forName(encoding);
		} catch (Exception e) {
			return InvalidArgument.create("Unknown encoding '" + encoding + "' for: " + what).asMaybe();
		}

		try (InputStream stream = in) {
			return Maybe.complete(new String(stream.readAllBytes(), charset));

		} catch (IOException e) {
			return InternalError.from(e).asMaybe();
		}
	}

	/** Losslessly projects an existing packaged source. The surrounding Resource metadata stays explicit. */
	public static ValueDescriptorExpressionProjection projection(ValueDescriptorSourceContext target) {
		return projection(target, resource -> false, source -> false);
	}

	/**
	 * Adds two explicit export decisions: which complete Resources are regenerable, and which classpath paths are known to belong to the target
	 * artifact. Neither decision is inferred from mutable Resource metadata.
	 */
	public static ValueDescriptorExpressionProjection projection(ValueDescriptorSourceContext target, Predicate<? super Resource> regenerableResource,
			Predicate<? super ClassPathSource> artifactClassPathSource) {

		return (inferredType, value) -> {
			if (value instanceof Resource resource && regenerableResource.test(resource)) {
				SourceLocation location = sourceLocation(resource.getResourceSource(), target, artifactClassPathSource);
				if (location == null)
					return null;

				PackagedResource descriptor = PackagedResource.T.create();
				configureLocation(descriptor::setPath, descriptor::setArtifact, location, target);
				return descriptor;
			}

			SourceLocation location = sourceLocation(value, target, artifactClassPathSource);
			if (location == null)
				return null;

			PackagedSource descriptor = PackagedSource.T.create();
			configureLocation(descriptor::setPath, descriptor::setArtifact, location, target);
			return descriptor;
		};
	}

	private static SourceLocation sourceLocation(Object value, ValueDescriptorSourceContext target,
			Predicate<? super ClassPathSource> artifactClassPathSource) {

		if (value instanceof com.braintribe.model.resource.source.PackagedSource source)
			return valid(source.getArtifact(), source.getPath()) ? new SourceLocation(source.getArtifact(), source.getPath()) : null;

		if (value instanceof ClassPathSource source)
			if (artifactClassPathSource.test(source) && source.getPath() != null && !source.getPath().trim().isEmpty())
				return new SourceLocation(target == null ? null : target.artifact(), source.getPath());

		return null;
	}

	private static boolean valid(String artifact, String path) {
		return artifact != null && !artifact.trim().isEmpty() && path != null && !path.trim().isEmpty();
	}

	private static void configureLocation(Consumer<String> pathSetter, Consumer<String> artifactSetter, SourceLocation source,
			ValueDescriptorSourceContext target) {

		if (target != null && source.artifact != null && source.artifact.equals(target.artifact()) && target.path() != null
				&& !target.path().trim().isEmpty()) {
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
			return InvalidArgument.create("A packaged resource expression requires a non-empty path").asMaybe();

		String artifact = explicitArtifact;
		String candidate = configuredPath.replace('\\', '/');
		boolean sourceRelative = candidate.startsWith("./") || candidate.startsWith("../") || candidate.equals(".") || candidate.equals("..");

		if (artifact == null || artifact.trim().isEmpty()) {
			if (source == null || source.artifact() == null || source.artifact().trim().isEmpty())
				return InvalidArgument.create("A packaged resource has no owning artifact context: " + configuredPath).asMaybe();

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
			return InvalidArgument.create("Packaged resource path escapes its artifact: " + configuredPath).asMaybe();

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

		@Override
		public String toString() {
			return artifact + ":" + path;
		}
	}
}

package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Common supertype for all values an {@code OutputNode} may emit.
 *
 * <p>{@link SafeOutput} is the text-capable branch understood by every sink (a string becomes
 * plain text). Document-oriented sinks additionally understand document-fragment derivatives
 * (e.g. run, image, embedded-document outputs) that are not reducible to a string. Keeping
 * {@code Output} as the shared root lets the same {@code OutputNode} carry either branch, while
 * each sink advertises which output types it supports.
 */
public interface Output extends GenericEntity {
	EntityType<Output> T = EntityTypes.T(Output.class);
}

package com.braintribe.messaging.jdbc;

import com.braintribe.model.resource.Resource;

/**
 * The marshalled {@link com.braintribe.model.messaging.Message} together with the values we store in extra columns, i.e. everything needed to insert a
 * row.
 * <p>
 * Exactly one of {@link #bodyInline} and {@link #bodyBlob} is set. See {@link #INLINE_BODY_CHARS_LIMIT}.
 *
 * @author peter.gazdik
 */
public class JdbcMessageEnvelope {

	/**
	 * Maximum length of a {@link #bodyInline} body, in characters.
	 * <p>
	 * The point of an inline body is that the insert trigger can pass it along in the notification, so the receiving node needs no extra query. The
	 * limit therefore comes from <tt>pg_notify</tt>, whose payload must stay below 8000 bytes.
	 * <p>
	 * The body is Base64, i.e. pure ASCII, so its character count is also its byte count. Everything else in the payload is not ours to control -
	 * destination name, node id and application id are arbitrary text, possibly multi-byte, plus JSON escaping and structure - hence the generous
	 * reserve rather than a limit close to 8000.
	 * <p>
	 * Base64 turns N bytes into 4 * ceil(N / 3) characters, so this corresponds to a marshalled message of roughly 3000 bytes.
	 */
	public static final int INLINE_BODY_CHARS_LIMIT = 4000;

	/** Base64 encoded body, small enough to travel within the insert notification. Mutually exclusive with {@link #bodyBlob}. */
	public String bodyInline;

	/** Body as binary data, streamed rather than held in memory. Mutually exclusive with {@link #bodyInline}. */
	public Resource bodyBlob;

	public String addresseeNodeId;
	public String addresseeAppId;
	public long expiration;
}

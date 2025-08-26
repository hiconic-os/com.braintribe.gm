package com.braintribe.messaging.jdbc;

/**
 * @author peter.gazdik
 */
public class JdbcMessageEnvelope {
	public String body;
	public String addresseeNodeId;
	public String addresseeAppId;
	public long expiration;
}

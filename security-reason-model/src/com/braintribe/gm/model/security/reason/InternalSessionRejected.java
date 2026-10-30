package com.braintribe.gm.model.security.reason;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * The session id belongs to an internal (system) session of another node.
 * <p>
 * An internal session exists only in the memory of the node that created it, and only that node can authenticate it. A caller that sends
 * its id, e.g. in an HTTP call to the own services URL, only succeeds when the load balancer happens to pick the creating node. Such a
 * call must instead be evaluated in-process, or be sent to other nodes through a mechanism that does not transport the session id (e.g.
 * messaging).
 */
public interface InternalSessionRejected extends InvalidSession {
	EntityType<InternalSessionRejected> T = EntityTypes.T(InternalSessionRejected.class);
}

package com.braintribe.gm.model.security.reason;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Authentication succeeded and an explicitly requested approval is waiting for a decision. */
public interface ApprovalPending extends ApprovalRequired {

	EntityType<ApprovalPending> T = EntityTypes.T(ApprovalPending.class);
}

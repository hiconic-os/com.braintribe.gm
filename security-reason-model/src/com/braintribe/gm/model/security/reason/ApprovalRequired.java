package com.braintribe.gm.model.security.reason;

import java.util.Date;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Authentication succeeded, but an additional approval is required. */
public interface ApprovalRequired extends AuthenticationFailure {

	EntityType<ApprovalRequired> T = EntityTypes.T(ApprovalRequired.class);

	String getApprovalRequestId();
	void setApprovalRequestId(String approvalRequestId);

	Date getExpiryDate();
	void setExpiryDate(Date expiryDate);
}

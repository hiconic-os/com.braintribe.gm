package com.braintribe.gm.graphfetching.api.node;

import com.braintribe.gm.graphfetching.processing.fetch.FetchType;

public class FetchQualification {
	public final AbstractEntityGraphNode node;
	public final FetchType fetchType;
	
	public FetchQualification(AbstractEntityGraphNode node, FetchType fetchType) {
		super();
		this.node = node;
		this.fetchType = fetchType;
	}

	@Override
	public int hashCode() {
	    return 31 * fetchType.hashCode() + System.identityHashCode(node);
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null || getClass() != obj.getClass())
	        return false;

	    FetchQualification other = (FetchQualification) obj;
	    return fetchType == other.fetchType && node == other.node;
	}

	@Override
	public String toString() {
		return "FetchQualification [node=" + node + ", fetchType=" + fetchType + "]";
	}
	
	
}

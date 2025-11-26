package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.model.generic.GenericEntity;

public class EntityIdm {
    public final GenericEntity entity;
    private final Set<FetchQualification> handledQualifications = ConcurrentHashMap.newKeySet(5);

    public EntityIdm(GenericEntity entity) {
        this.entity = entity;
    }

    public boolean isHandled(FetchQualification fetchQualification) {
        return handledQualifications.contains(fetchQualification);
    }

    public boolean addHandled(FetchQualification fetchQualification) {
    		return handledQualifications.add(fetchQualification);
    }
}

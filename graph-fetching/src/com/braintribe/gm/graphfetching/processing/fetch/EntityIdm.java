package com.braintribe.gm.graphfetching.processing.fetch;

import com.braintribe.model.generic.GenericEntity;
import java.util.ArrayList;
import java.util.List;

public class EntityIdm {
    public final GenericEntity entity;
    private final List<FetchQualification> handledQualifications = new ArrayList<>(2);

    public EntityIdm(GenericEntity entity) {
        this.entity = entity;
    }

    public boolean isHandled(FetchQualification fetchQualification) {
        return handledQualifications.contains(fetchQualification);
    }

    public void addHandled(FetchQualification fetchQualification) {
        if (!handledQualifications.contains(fetchQualification)) {
            handledQualifications.add(fetchQualification);
        }
    }
}

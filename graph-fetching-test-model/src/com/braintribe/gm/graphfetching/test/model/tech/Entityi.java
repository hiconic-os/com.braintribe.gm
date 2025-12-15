// Entityi.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entityi extends HasScalars {

    EntityType<Entityi> T = EntityTypes.T(Entityi.class);

    // TO_ONE
    String entityj = "entityj";

    Entityj getEntityj();
    void setEntityj(Entityj entityj);

    // Collections
    // Set<long> -> Long
    String setLong = "setLong";
    Set<Long> getSetLong();
    void setSetLong(Set<Long> setLong);
}

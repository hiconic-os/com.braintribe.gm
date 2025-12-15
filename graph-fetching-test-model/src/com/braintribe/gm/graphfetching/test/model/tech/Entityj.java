// Entityj.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entityj extends HasScalars {

    EntityType<Entityj> T = EntityTypes.T(Entityj.class);

    // Set<double> -> Double
    String setDouble = "setDouble";
    Set<Double> getSetDouble();
    void setSetDouble(Set<Double> setDouble);
}

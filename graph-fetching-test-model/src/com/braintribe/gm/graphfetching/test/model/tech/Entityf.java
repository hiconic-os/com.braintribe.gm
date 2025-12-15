// Entityf.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entityf extends HasScalars {

    EntityType<Entityf> T = EntityTypes.T(Entityf.class);

    // TO_ONE
    String entityg = "entityg";
    String entityh = "entityh";
    String entityi = "entityi";
    String entityj = "entityj";

    Entityg getEntityg();
    void setEntityg(Entityg entityg);

    Entityh getEntityh();
    void setEntityh(Entityh entityh);

    Entityi getEntityi();
    void setEntityi(Entityi entityi);

    Entityj getEntityj();
    void setEntityj(Entityj entityj);

    // Collections
    // Map<Entityc, double> -> Double
    String mapEntityhDouble = "mapEntityhDouble";
    Map<Entityh, Double> getMapEntityhDouble();
    void setMapEntityhDouble(Map<Entityh, Double> mapEntityhDouble);

    // Set<string> -> String
    String setString = "setString";
    Set<String> getSetString();
    void setSetString(Set<String> setString);
}

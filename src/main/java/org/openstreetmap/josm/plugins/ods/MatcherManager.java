package org.openstreetmap.josm.plugins.ods;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.matching.AddressNodeMatcher;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatcher;

public class MatcherManager {
    private BuildingMatcher buildingMatcher;
    private AddressNodeMatcher addressNodeMatcher;
    
    private Map<Class<? extends Object>, Matcher<? extends Object>> matchers = new LinkedHashMap<>();
    
    public MatcherManager(OdsModule module) {
        buildingMatcher = new BuildingMatcher(module);
        addressNodeMatcher = new AddressNodeMatcher(module);
        matchers.put(Building.class, buildingMatcher);
        matchers.put(AddressNode.class, addressNodeMatcher);
    }
    
    public Collection<Matcher<? extends Object>> getMatchers() {
        return matchers.values();
    }

    @SuppressWarnings("unchecked")
    public <E> Matcher<? extends E> getMatcher(Class<E> clazz) {
        return (Matcher<? extends E>) matchers.get(clazz);
    }

    public void reset() {
        for (Matcher<?> matcher : matchers.values()) {
            matcher.reset();
        }
    }
}

package org.openstreetmap.josm.plugins.ods;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public class MatcherManager {
    private Map<Class<? extends Object>, Matcher<? extends Object>> matchers = new LinkedHashMap<>();
    
    public MatcherManager(OdsModule module) {
        reset();
    }
    
    public void reset() {
        for (Matcher<?> matcher : getMatchers()) {
            matcher.reset();
        }
    }
    
    public Collection<Matcher<? extends Object>> getMatchers() {
        return matchers.values();
    }

    public <E extends Entity> void registerMatcher(Matcher<E> matcher) {
        matchers.put(matcher.getType(), matcher);
    }
    
    @SuppressWarnings("unchecked")
    public <E> Matcher<? extends E> getMatcher(Class<E> clazz) {
        return (Matcher<? extends E>) matchers.get(clazz);
    }
}

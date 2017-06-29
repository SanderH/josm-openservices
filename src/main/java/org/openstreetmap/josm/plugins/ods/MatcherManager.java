package org.openstreetmap.josm.plugins.ods;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MatcherManager {
    private final Set<Matcher> matchers = new HashSet<>();

    public MatcherManager(OdsModule module) {
        reset();
    }

    public void reset() {
        for (Matcher matcher : getMatchers()) {
            matcher.reset();
        }
    }

    public Collection<Matcher> getMatchers() {
        return matchers;
    }

    public void registerMatcher(Matcher matcher) {
        matchers.add(matcher);
    }

    //    @SuppressWarnings("unchecked")
    //    public Matcher getMatcher(Class<E> clazz) {
    //        return (Matcher<? extends E>) matchers.get(clazz);
    //    }
}

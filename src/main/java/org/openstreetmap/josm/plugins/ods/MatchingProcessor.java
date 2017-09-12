package org.openstreetmap.josm.plugins.ods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MatchingProcessor {
    private final List<Matcher> matchers = new ArrayList<>();

    public MatchingProcessor(OdsModule module) {
        reset();
    }

    public void run() {
        for (Matcher matcher : getMatchers()) {
            matcher.run();
        }
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

package org.openstreetmap.josm.plugins.ods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
public class MatchingProcessor {
    private final List<MatchTask> matchTasks = new ArrayList<>();

    public MatchingProcessor(OdsModule module) {
        reset();
    }

    public void run() {
        for (MatchTask matchTask : getMatchers()) {
            //            matchTask.run();
        }
    }

    public void reset() {
        for (MatchTask matchTask : getMatchers()) {
            matchTask.reset();
        }
    }

    public Collection<MatchTask> getMatchers() {
        return matchTasks;
    }

    public void registerMatcher(MatchTask matchTask) {
        matchTasks.add(matchTask);
    }

    //    @SuppressWarnings("unchecked")
    //    public MatchTask getMatcher(Class<E> clazz) {
    //        return (MatchTask<? extends E>) matchTasks.get(clazz);
    //    }
}

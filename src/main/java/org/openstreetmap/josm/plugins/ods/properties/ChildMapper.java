package org.openstreetmap.josm.plugins.ods.properties;

public interface ChildMapper<S, T> {
    Object map(S source, T target);
}

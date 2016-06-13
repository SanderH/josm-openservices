package org.openstreetmap.josm.plugins.ods.properties;

import java.util.function.Consumer;

public interface EntityMapper<T1, T2> {
    public void map(T1 source, T2 target);
    public T2 map(T1 source);
    public void mapAndConsume(T1 source, Consumer<Object> consumer);
}

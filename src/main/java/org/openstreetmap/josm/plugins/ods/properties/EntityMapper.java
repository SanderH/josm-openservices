package org.openstreetmap.josm.plugins.ods.properties;

public interface EntityMapper<T1, T2> {
    //    public void map(T1 source, T2 target);
    public T2 map(T1 source);
    //    public void map(T1 source, Repository repository);
    //    public void mapAndConsume(T1 source, Consumer<Object> consumer);
}

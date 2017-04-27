package org.openstreetmap.josm.plugins.ods.storage;

public interface IndexKey<T> {
    public Class<T> getBaseClass();

    public Object getKey(T t);

    public <T2 extends T> IndexKey<T2> forSubClass(Class<T2> subClass);

    //    public Function<T, Object> getFunction();
}

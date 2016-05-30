package org.openstreetmap.josm.plugins.ods.properties.transform;

import java.util.function.Function;

public interface TypeTransform<S, T> {
    public Class<S> getSourceType();
    public Class<T> getTargetType();
    public Function<S, T> getFunction();

    public T apply(S source);
}

package org.openstreetmap.josm.plugins.ods.properties.transform;

import java.util.function.Function;

public class SimpleTypeTransform<S, T> implements TypeTransform<S, T> {
    private Class<S> sourceType;
    private Class<T> targetType;
    private Function<S, T> function;

    public SimpleTypeTransform(Class<S> sourceType, Class<T> targetType, Function<S, T> function) {
        super();
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.function = function;
    }

    @Override
    public Class<S> getSourceType() {
        return sourceType;
    }

    @Override
    public Class<T> getTargetType() {
        return targetType;
    }

    public Function<S, T> getFunction() {
        return function;
    }

    @Override
    public T apply(S source) {
        if (source == null) {
            return null;
        }
        return function.apply(source);
    }
}

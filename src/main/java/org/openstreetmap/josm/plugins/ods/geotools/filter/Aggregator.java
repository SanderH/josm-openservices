package org.openstreetmap.josm.plugins.ods.geotools.filter;

import java.util.function.Consumer;

public interface Aggregator<T, S> extends Consumer<Object> {
    public Class<T> getSourceType();
    public S getResult();
}

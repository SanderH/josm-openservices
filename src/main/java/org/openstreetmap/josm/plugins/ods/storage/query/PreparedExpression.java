package org.openstreetmap.josm.plugins.ods.storage.query;

public interface PreparedExpression<T> {
    Object evaluate(T object);
}

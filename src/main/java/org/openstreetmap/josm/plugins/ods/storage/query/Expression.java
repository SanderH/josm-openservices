package org.openstreetmap.josm.plugins.ods.storage.query;

public interface Expression {

    public <T> PreparedExpression<T> prepare(Class<T> type);
    // Marker interface
}
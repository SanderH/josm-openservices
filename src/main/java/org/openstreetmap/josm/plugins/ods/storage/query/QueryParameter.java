package org.openstreetmap.josm.plugins.ods.storage.query;

public interface QueryParameter extends Expression {
    public String getName();
    @Override
    public <T> PreparedParameter prepare(Class<T> type);
}

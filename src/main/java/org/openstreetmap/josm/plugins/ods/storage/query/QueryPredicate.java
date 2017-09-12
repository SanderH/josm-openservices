package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.Collection;
import java.util.function.Predicate;

public interface QueryPredicate {
    public Collection<QueryParameter> getParameters();

    public Collection<AttributeExpression> getAttributes();

    public <T> Predicate<T> prepare(Class<T> type);
}
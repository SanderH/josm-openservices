package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.function.Predicate;

public interface PreparedQuery<T> {

    public Class<T> getResultType();

    public Predicate<T> getPredicate();
}

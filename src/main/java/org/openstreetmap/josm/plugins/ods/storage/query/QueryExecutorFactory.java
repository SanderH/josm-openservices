package org.openstreetmap.josm.plugins.ods.storage.query;

import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class QueryExecutorFactory {
    public static <T> QueryExecutor<T> create(Repository repo, Query<T> query) {
        PreparedQuery<T> preparedQuery = query.prepare();
        return new NoFilterQueryExecutor<>(repo, preparedQuery);
    }

}
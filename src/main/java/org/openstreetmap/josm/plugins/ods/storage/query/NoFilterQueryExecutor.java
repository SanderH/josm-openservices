package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.storage.ObjectStore;
import org.openstreetmap.josm.plugins.ods.storage.Repository;
import org.openstreetmap.josm.plugins.ods.storage.query.QueryParameterImpl.PreparedParameterImpl;

public class NoFilterQueryExecutor<T> implements QueryExecutor<T> {
    private final Repository repo;
    private final PreparedQuery<T> query;
    private final Collection<PreparedParameterImpl> preparedParameterImpls;

    public NoFilterQueryExecutor(Repository repo, PreparedQuery<T> query) {
        this(repo, query, Collections.emptyList());
    }

    public NoFilterQueryExecutor(
            Repository repo,
            PreparedQuery<T> query,
            Collection<PreparedParameterImpl> preparedParameterImpls) {
        this.repo = repo;
        this.query = query;
        this.preparedParameterImpls = preparedParameterImpls;
    }

    //    @Override
    //    public Class<T> getResultType() {
    //        return query.getResultType();
    //    }

    @Override
    public ResultSet<T> run(Map<String, Object> values) {
        for (PreparedParameter parameter : preparedParameterImpls) {
            parameter.setParameters(values);
        }
        ObjectStore<T> store = repo.getStore(query.getResultType());
        if (store == null) {
            return ResultSet.<T> emptyResultSet();
        }
        return store.getAllResults(true).filter(query.getPredicate());
    }
}

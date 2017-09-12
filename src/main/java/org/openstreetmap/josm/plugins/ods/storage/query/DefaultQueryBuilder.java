package org.openstreetmap.josm.plugins.ods.storage.query;

import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class DefaultQueryBuilder<T> implements QueryBuilder<T> {
    private final Repository repo;
    private final Class<T> resultType;
    private QueryPredicate filter;

    public DefaultQueryBuilder(Repository repo, Class<T> resultType) {
        super();
        this.repo = repo;
        this.resultType = resultType;
    }

    @Override
    public Class<T> getResultType() {
        return resultType;
    }

    //    @Override
    //    public <P> QueryParameter<P> addParameter(String name, Class<P> type) {
    //        QueryParameter<P> parameter = new QueryParameterImpl<>(name, type);
    //        queryParameters.put(name, parameter);
    //        return parameter;
    //    }

    @Override
    public void setFilter(QueryPredicate queryPredicate) {
        this.filter = queryPredicate;
    }

    @Override
    public Query<T> getQuery() {
        return new QueryImpl<>(repo, resultType, filter);
    }

    public static <T1> Query<T1> getSimpleQuery(Repository repo, Class<T1> type) {
        QueryBuilder<T1> builder = new DefaultQueryBuilder<>(repo, type);
        return builder.getQuery();
    }
}
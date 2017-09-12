package org.openstreetmap.josm.plugins.ods.storage.query;

public interface QueryBuilder<T> {
    public Class<T> getResultType();
    //    public <P> QueryParameter<P> addParameter(String name, Class<P> type);
    public void setFilter(QueryPredicate queryPredicate);
    public Query<T> getQuery();
}

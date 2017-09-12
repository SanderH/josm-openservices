package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class QueryImpl<T> implements Query<T> {
    private final Repository repo;
    private final Class<T> resultType;
    //    private final Map<String, QueryParameter> queryParameters;
    private final QueryPredicate filter;

    public QueryImpl(Repository repo, Class<T> resultType, QueryPredicate filter) {
        this.repo = repo;
        this.resultType = resultType;
        this.filter = filter;
    }


    @Override
    public Repository getRepository() {
        return repo;
    }


    @Override
    public Class<T> getResultType() {
        return resultType;
    }

    @Override
    public QueryPredicate getFilter() {
        return filter;
    }

    @Override
    public boolean hasFilter() {
        return filter != null;
    }

    @Override
    public PreparedQuery<T> prepare() {
        return new PreparedQueryFactory<>(this).create();
    }

    @Override
    public ResultSet<T> run() {
        return repo.run(this);
    }


    @Override
    public Stream<? extends T> stream() {
        return run().stream();
    }


    @Override
    public Iterator<? extends T> iterator() {
        return run().iterator();
    }


    @Override
    public void forEach(Consumer<T> consumer) {
        stream().forEach(consumer);
    }

    //    @Override
    //    public boolean hasParameters() {
    //        return !queryParameters.isEmpty();
    //    }

    //    @Override
    //    public Collection<QueryParameter> getParameters() {
    //        return queryParameters.values();
    //    }
}

package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.openstreetmap.josm.plugins.ods.storage.query.AttributeExpression.PreparedAttributeExpression;

public class PreparedQueryFactory<T> {
    private final Query<T> query;
    private final Map<String, PreparedParameter> parameters = new HashMap<>();
    private final Map<String, PreparedAttributeExpression<T>> attributes = new HashMap<>();
    private Predicate<T> predicate;

    public PreparedQueryFactory(Query<T> query) {
        super();
        this.query = query;
    }

    public PreparedQuery<T> create() {
        Class<T> type = query.getResultType();
        for (QueryParameter parameter : query.getFilter().getParameters()) {
            PreparedParameter preparedParameter = parameter.prepare(type);
            parameters.put(parameter.getName(), preparedParameter);
        }
        for (AttributeExpression attribute : query.getFilter().getAttributes()) {
            PreparedAttributeExpression<T> preparedAttribute = attribute.prepare(type);
            attributes.put(attribute.getAttribute(), preparedAttribute);
        }
        predicate = query.getFilter().prepare(type);
        return new PreparedQueryImpl(parameters, attributes);
    }

    class PreparedQueryImpl implements PreparedQuery<T> {
        private final Map<String, PreparedParameter> params;
        private final Map<String, PreparedAttributeExpression<T>> attrs;
        //        private final Predicate<T> predicate;

        public PreparedQueryImpl(Map<String, PreparedParameter> params,
                Map<String, PreparedAttributeExpression<T>> attrs) {
            super();
            this.params = params;
            this.attrs = attrs;
            //            this.predicate = predicate;
        }

        public Map<String, PreparedParameter> getParameters() {
            return params;
        }

        public Map<String, PreparedAttributeExpression<T>> getAttrs() {
            return attrs;
        }

        @Override
        @SuppressWarnings("synthetic-access")
        public Predicate<T> getPredicate() {
            return predicate;
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public Class<T> getResultType() {
            return query.getResultType();
        }
    }
}

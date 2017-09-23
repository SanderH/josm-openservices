package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public abstract class AbstractQueryPredicate implements QueryPredicate {

    @Override
    public QueryPredicate AND(QueryPredicate predicate) {
        return new And(this, predicate);
    }

    public static class And extends AbstractQueryPredicate {
        private final QueryPredicate p1;
        private final QueryPredicate p2;
        private final Collection<QueryParameter> parameters;
        private final Collection<AttributeExpression> attributes;

        public And(QueryPredicate p1, QueryPredicate p2) {
            super();
            this.p1 = p1;
            this.p2 = p2;
            int n = p1.getParameters().size() + p2.getParameters().size();
            this.parameters = new ArrayList<>(n);
            parameters.addAll(p1.getParameters());
            parameters.addAll(p2.getParameters());
            n = p1.getAttributes().size() + p2.getAttributes().size();
            this.attributes = new ArrayList<>(n);
            attributes.addAll(p1.getAttributes());
            attributes.addAll(p2.getAttributes());
        }

        @Override
        public Collection<QueryParameter> getParameters() {
            return this.parameters;
        }

        @Override
        public Collection<AttributeExpression> getAttributes() {
            return this.attributes;
        }

        @Override
        public <T> Predicate<T> prepare(Class<T> type) {
            Predicate<T> pp1 = p1.prepare(type);
            Predicate<T> pp2 = p2.prepare(type);
            return (obj -> {
                return pp1.test(obj) && pp2.test(obj);
            });
        }
    }
}

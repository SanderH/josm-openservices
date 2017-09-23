package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.storage.Repository;

public interface Query<T> {
    public static QueryPredicate TRUE = new True();

    public Class<T> getResultType();

    public Repository getRepository();

    //    public boolean hasParameters();

    //    public Collection<QueryParameter> getParameters();

    public QueryPredicate getFilter();

    public boolean hasFilter();

    public PreparedQuery<T> prepare();

    public ResultSet<T> run();

    public Stream<? extends T> stream();

    public Iterator<? extends T> iterator();

    public List<? extends T> toList();

    public Set<? extends T> toSet();

    public void forEach(Consumer<T> consumer);

    public static Expression CONST(Object o) {
        return new ConstantExpression(o);
    }

    public static QueryParameter PARAM(String name) {
        return new QueryParameterImpl(name);
    }

    public static AttributeExpression ATTR(String name) {
        return new AttributeExpression(name);
    }

    public static QueryPredicate EQUALS(Object o, Expression e2) {
        return new Equals(CONST(o), e2);
    }

    public static QueryPredicate EQUALS(Expression e1, Object o) {
        return new Equals(e1, CONST(o));
    }

    public static QueryPredicate EQUALS(Expression e1, Expression e2) {
        return new Equals(e1, e2);
    }

    public static class True extends AbstractQueryPredicate {

        @Override
        public Collection<QueryParameter> getParameters() {
            return Collections.emptyList();
        }

        @Override
        public Collection<AttributeExpression> getAttributes() {
            return Collections.emptyList();
        }

        @Override
        public <T> Predicate<T> prepare(Class<T> type) {
            return new Predicate<T>() {

                @Override
                public boolean test(Object t) {
                    return true;
                }
            };
        }
    }

    public static class Equals extends AbstractQueryPredicate {
        private final Expression expr1;
        private final Expression expr2;

        public Equals(Expression expr1, Expression expr2) {
            super();
            this.expr1 = expr1;
            this.expr2 = expr2;
        }

        public Expression getExpr1() {
            return expr1;
        }

        public Expression getExpr2() {
            return expr2;
        }

        @Override
        public Collection<QueryParameter> getParameters() {
            return Query.getParameters(expr1, expr2);
        }

        @Override
        public Collection<AttributeExpression> getAttributes() {
            return Query.getAttributes(expr1, expr2);
        }

        @Override
        public <T> Predicate<T> prepare(Class<T> type) {
            PreparedExpression<T> p1 = expr1.prepare(type);
            PreparedExpression<T> p2 = expr2.prepare(type);
            return (obj -> {
                return Objects.equals(p1.evaluate(obj), p2.evaluate(obj));
            });
        }
    }

    static Collection<QueryParameter> getParameters(Expression ...expressions) {
        List<QueryParameter> parameters = new LinkedList<>();
        for (Expression expr : expressions) {
            if (expr instanceof QueryParameter) {
                parameters.add((QueryParameter) expr);
            }
        }
        return parameters;
    }

    static Collection<AttributeExpression> getAttributes(Expression ...expressions) {
        List<AttributeExpression> attributes = new LinkedList<>();
        for (Expression expr : expressions) {
            if (expr instanceof AttributeExpression) {
                attributes.add((AttributeExpression) expr);
            }
        }
        return attributes;
    }
}

package org.openstreetmap.josm.plugins.ods.storage.query;

public class ConstantExpression implements Expression, PreparedExpression<Object> {
    private final Object value;

    public ConstantExpression(Object value) {
        super();
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> PreparedExpression<T> prepare(Class<T> type) {
        return (PreparedExpression<T>) this;
    }

    @Override
    public Object evaluate(Object o) {
        return value;
    }
}

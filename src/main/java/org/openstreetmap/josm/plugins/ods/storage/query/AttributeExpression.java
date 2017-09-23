package org.openstreetmap.josm.plugins.ods.storage.query;

import org.openstreetmap.josm.plugins.ods.properties.PropertyGetter;
import org.openstreetmap.josm.plugins.ods.properties.pojo.PojoPropertyHandlerFactory;

public class AttributeExpression implements Expression {
    private final String attribute;
    final String[] path;

    public AttributeExpression(String attribute) {
        super();
        this.attribute = attribute;
        path = attribute.split("\\.");
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public <T> PreparedAttributeExpression<T> prepare(Class<T> type) {
        return new PreparedAttributeExpression<>(type);
    }

    public class PreparedAttributeExpression<T> implements PreparedExpression<T> {

        private final PropertyGetter<T, ?> propertyGetter;
        //        private T object;

        public PreparedAttributeExpression(Class<T> type) {
            super();
            propertyGetter = PojoPropertyHandlerFactory.createGetter(type, path);
        }

        @Override
        public Object evaluate(T object) {
            return propertyGetter.get(object);
        }
    }
}
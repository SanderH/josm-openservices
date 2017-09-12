package org.openstreetmap.josm.plugins.ods.storage.query;

import org.openstreetmap.josm.plugins.ods.properties.pojo.PojoPropertyHandlerFactory.PojoPropertyHandler;

public class AttributeExpression implements Expression {
    //    private final Class<T> type;
    private final String attribute;

    public AttributeExpression(String attribute) {
        super();
        //        this.type = type;
        this.attribute = attribute;
    }

    //    public Class<T> getType() {
    //        return type;
    //    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public <T> PreparedAttributeExpression<T> prepare(Class<T> type) {
        return new PreparedAttributeExpression<>(type);
    }

    class PreparedAttributeExpression<T> implements PreparedExpression<T> {

        private final PojoPropertyHandler<T, ?> propertyHandler;
        //        private T object;


        public PreparedAttributeExpression(Class<T> type) {
            super();
            propertyHandler = new PojoPropertyHandler<>(type, Object.class, getAttribute());
        }

        //        public void setObject(T obj) {
        //            this.object = obj;
        //        }

        @Override
        public Object evaluate(T object) {
            return propertyHandler.get(object);
        }
    }
}
package org.openstreetmap.josm.plugins.ods.properties.pojo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openstreetmap.josm.plugins.ods.properties.PropertyGetter;
import org.openstreetmap.josm.plugins.ods.properties.PropertyHandler;

public class PojoPropertyHandlerFactory {

    public static <T1, T2> PropertyHandler<T1, T2> create(Class<T1> objectClass, Class<T2> attrClass, String name) {
        PropertyHandler<T1, T2> result = new PojoPropertyHandler<>(objectClass, attrClass, name);
        return result;
    }

    public static <T1> PropertyHandler<T1, ?> create(Class<T1> objType, String attributeName) {
        Method getter = PojoUtils.getAttributeGetter(objType, attributeName);
        if (getter != null) {
            Class<?> attrType = PojoUtils.getNonPrimitiveClass(getter.getReturnType());
            return new PojoPropertyHandler<>(objType, attrType, attributeName);
        }
        return null;
    }

    public static <T1> PropertyGetter<T1, ?> createGetter(Class<T1> objType, String[] path) {
        if (path.length == 1) {
            return create(objType, path[0]);
        }
        int i = 0;
        Method[] getters = new Method[path.length];
        Class<?> type = objType;
        while (i < path.length) {
            getters[i] = PojoUtils.getAttributeGetter(type, path[i]);
            type = getters[i].getReturnType();
            i++;
        }
        return new ChainedPropertyGetter<>(objType, getters);
    }

    private static class PojoPropertyHandler<T1, T2> implements PropertyHandler<T1, T2> {
        private final Class<T2> attrType;
        private final Method setter;
        private final Method getter;

        public PojoPropertyHandler(Class<T1> objType, Class<T2> attrType, String name) {
            super();
            this.attrType = attrType;
            getter = PojoUtils.getAttributeGetter(objType, name, attrType);
            setter = PojoUtils.getAttributeSetter(objType, name, attrType);
        }

        @Override
        public Class<T2> getType() {
            return attrType;
        }

        @Override
        public void set(T1 feature, T2 value) {
            if (setter != null) {
                try {
                    setter.invoke(feature, value);
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public T2 get(T1 obj) {
            if (getter != null) {
                try {
                    @SuppressWarnings("unchecked")
                    T2 result = (T2) getter.invoke(obj);
                    return result;
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }

    private static class ChainedPropertyGetter<T1, T2> implements PropertyGetter<T1, T2> {
        @SuppressWarnings("unused")
        private final Class<T1> type;
        private final Method[] getters;

        public ChainedPropertyGetter(Class<T1> type, Method[] getters) {
            this.type = type;
            this.getters = getters;
        }
        @SuppressWarnings("unchecked")
        @Override
        public T2 get(T1 obj) {
            Object result = obj;
            for (int i = 0; i < getters.length ; i++) {
                try {
                    result = getters[i].invoke(result);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return (T2) result;
        }

    }
}

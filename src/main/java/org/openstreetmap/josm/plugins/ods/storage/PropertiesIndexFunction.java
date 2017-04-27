package org.openstreetmap.josm.plugins.ods.storage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class PropertiesIndexFunction<T> implements Function<T, Object> {
    private final Class<T> baseClass;
    private final List<String> properties;
    private final Method[] getters;

    public PropertiesIndexFunction(Class<T> baseClass,
            List<String> properties) {
        super();
        this.baseClass = baseClass;
        this.properties = properties;
        this.getters = createGetters();
    }

    @Override
    public Object apply(T t) {
        try {
            if (getters.length == 1) {
                return getters[0].invoke(t);
            }
            List<Object> key = new ArrayList<>(getters.length);
            for (int i=0; i<getters.length; i++) {
                key.add(getters[i].invoke(t));
            }
            return key;
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Method[] createGetters() {
        Method[] theGetters = new Method[properties.size()];
        int i=0;
        for (String property : properties) {
            theGetters[i++] = createGetter(property);
        }
        return theGetters;
    }

    private Method createGetter(String property) {
        for (Method method : baseClass.getMethods()) {
            if (method.getParameterCount() > 0) {
                continue;
            }
            Class<?> returnType = method.getReturnType();
            if (returnType == Void.class) {
                continue;
            }
            String name = method.getName();
            if (name.startsWith("get") && name.substring(3).equalsIgnoreCase(property)) {
                return method;
            }
            if (name.startsWith("is") && name.substring(2).equalsIgnoreCase(property)) {
                return method;
            }
        }
        throw new IllegalArgumentException(String.format(
                "Propert '%s' in class '%s' could not be found" , baseClass.getName(), property));
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseClass, properties);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof PropertiesIndexFunction)) {
            return false;
        }
        PropertiesIndexFunction<?> other = (PropertiesIndexFunction<?>) obj;
        return Objects.equals(other.baseClass, baseClass) &&
                Objects.equals(other.properties, properties);
    }
}

package org.openstreetmap.josm.plugins.ods.storage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.openstreetmap.josm.plugins.ods.properties.pojo.PojoUtils;

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

    // TODO Make sure all properties exist
    private Method[] createGetters() {
        Method[] theGetters = new Method[properties.size()];
        int i=0;
        for (String property : properties) {
            theGetters[i++] = PojoUtils.getAttributeGetter(baseClass, property);
        }
        return theGetters;
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

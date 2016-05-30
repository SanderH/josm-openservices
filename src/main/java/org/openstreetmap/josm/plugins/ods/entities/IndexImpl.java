package org.openstreetmap.josm.plugins.ods.entities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IndexImpl<T> implements Index<T> {
    private Map<Object, List<T>> map = new HashMap<>();
    private final Class<T> clazz;
    private final List<String> properties;
    private final Method[] getters;
//    private final Function<T, ?>[] getters;
    
    @SafeVarargs
    public IndexImpl(Class<T> clazz, String ... properties) {
        super();
        this.clazz = clazz;
        this.properties = Arrays.asList(properties);
        this.getters = createGetters();
    }
    
    @Override
    public List<String> getProperties() {
        return properties;
    }

    private Method[] createGetters() {
        Method[] theGetters = new Method[properties.size()];
        for (int i=0; i<properties.size(); i++) {
            theGetters[i] = createGetter(properties.get(i));
        }
        return theGetters;
    }

    private Method createGetter(String property) {
        for (Method method : clazz.getMethods()) {
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
        return null;
    }

    @Override
    public Class<T> getType() {
        return clazz;
    }


    @Override
    public boolean isUnique() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     */
    @Override
    public void insert(T entity) {
        Object key = getKey(entity);
        if (key != null) {
            List<T> list = map.get(key);
            if (list == null) {
                list = new LinkedList<>();
                map.put(key, list);
            }
            list.add(entity);
        }
    }
    
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#get(U)
     */
    public List<T> getAll(Object key) {
        List<T> result = map.get(key);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }
    
    @Override
    public void remove(T entity) {
        map.remove(getKey(entity));
    }

    @Override
    public Object getKey(T entity) {
        try {
            if (getters.length == 1) {
                return getters[0].invoke(entity);
            }
            else {
                List<Object> key = new ArrayList<>(getters.length);
                for (int i=0; i<getters.length; i++) {
                    key.add(getters[i].invoke(entity));
                }
                return key;
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void clear() {
        map.clear();
    }
}

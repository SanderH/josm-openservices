package org.openstreetmap.josm.plugins.ods.entities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class UniqueIndexImpl<T> implements UniqueIndex<T> {
    private Map<Object, T> map = new HashMap<>();
    private final Class<T> clazz;
    private final List<String> properties;
    private final DuplicateValueListener listener;
    private Method[] getters;
//    private String[] properties; 
    
    public UniqueIndexImpl(Class<T> clazz, String ... properties) {
        this(clazz, null, properties);
    }
    
    public UniqueIndexImpl(Class<T> clazz, DuplicateValueListener listener, String ... properties) {
        this.clazz = clazz;
        this.listener = listener;
        this.properties = Arrays.asList(properties);
        this.getters = createGetters();
    }
    
    
    @Override
    public Class<T> getType() {
        return clazz;
    }

    @Override
    public List<String> getProperties() {
        return properties;
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     */
    @Override
    public void insert(T entity) {
        Object key = getKey(entity);
        if (key != null) {
            T existing = get(key);
            if (existing == null) {
                map.put(key, entity);
            }
            else {
                if (listener != null) {
                    listener.duplicateKey(entity, key, existing);
                }
            }
        }
    }
    
    @Override
    public boolean contains(T entity) {
        return map.containsKey(getKey(entity));
    }

    //    private Method[] createGetters() {
//        Method[] getters = new Method[properties.length];
//        try {
//            for (int i=0; i< properties.length; i++) {
//                getters[i] = clazz.getMethod(getGetterName(i));
//            }
//            return getters;
//        } catch (NoSuchMethodException | SecurityException e) {
//            e.printStackTrace();
//            throw new RuntimeException();
//        }
//    }
//    
    @Override
    public Iterator<T> iterator() {
        return map.values().iterator();
    }

    @Override
    public Stream<T> stream() {
        return map.values().stream();
    }

//    public T get(T entity) {
//        return get(getKey(entity));
//    }
//    
    @Override
    public T get(Object key) {
        return map.get(key);
    }
    
    @Override
    public Iterable<T> getAll() {
        return map.values();
    }
    
    @Override
    public List<T> getAll(Object key) {
        T result = map.get(key);
        if (result == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(result);
    }
    
    @Override
    public void remove(T entity) {
        map.remove(getKey(entity));
    }

//    public void removeByKey(Object key) {
//        map.remove(key);
//    }
//

    @Override
    public Object getKey(T entity) {
        try {
            if (getters.length == 1) {
                return getters[0].invoke(entity);
            }
            List<Object> key = new ArrayList<>(getters.length);
            for (int i=0; i<getters.length; i++) {
                key.add(getters[i].invoke(entity));
            }
            return key;
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

//    private String getGetterName(int i) {
//        String property = properties[i];
//        return "get" + property.substring(0, 1).toUpperCase() +
//                    property.substring(1);
//    }
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
}

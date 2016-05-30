package org.openstreetmap.josm.plugins.ods.entities;

import java.util.List;

public interface Index<T> {

    public Class<T> getType();
    
    public List<String> getProperties();
    
    public boolean isUnique();
    
    public Object getKey(T entity);
    
    public void insert(T entity);
    
    public List<T> getAll(Object id);
    
    public void remove(T entity);

    public void clear();
}
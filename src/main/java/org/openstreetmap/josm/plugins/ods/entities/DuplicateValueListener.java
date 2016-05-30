package org.openstreetmap.josm.plugins.ods.entities;

public interface DuplicateValueListener {

    /**
     * Report a duplicate key on an attempt to add an Entity to a unique index
     * 
     * @param entity The entity that couldn't be added
     * @param key The entity's primary key
     * @param existing The existing entity
     */
    public <T> void duplicateKey(T entity, Object key, T existing);

}

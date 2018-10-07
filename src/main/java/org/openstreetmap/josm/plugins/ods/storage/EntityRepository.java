package org.openstreetmap.josm.plugins.ods.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;

public class EntityRepository {
    private final Map<Class<?>, EntityDao<? extends Entity>> main = new HashMap<>();
    private final Map<Class<?>, EntityDao<? extends Entity>> all = new HashMap<>();

    public EntityRepository(Collection<EntityDao<?>> daos) {
        for (EntityDao<?> dao : daos) {
            main.put(dao.getMainClass(), dao);
            all.put(dao.getMainClass(), dao);
        }
    }

    public <T extends Entity> EntityDao<T> getDao(Class<T> entityClass) {
        @SuppressWarnings("unchecked")
        EntityDao<T> dao = (EntityDao<T>) all.get(entityClass);
        return dao;
    }
}

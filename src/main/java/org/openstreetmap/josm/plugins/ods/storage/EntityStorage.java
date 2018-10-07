package org.openstreetmap.josm.plugins.ods.storage;

import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class EntityStorage<E extends Entity> implements EntityDao<E> {
    final Class<E> entityClass;
    final IdentitySet<E> all = new IdentitySet<>();
    private Quadtree quadTree = new Quadtree();

    public EntityStorage(Class<E> entityClass) {
        super();
        this.entityClass = entityClass;
    }

    @Override
    public void insert(E object) {
        all.add(object);
    }

    @Override
    public Stream<E> findAll() {
        return all.stream();
    }

    @Override
    public Stream<E> findByIntersection(Geometry geometry) {
        @SuppressWarnings("unchecked")
        Stream<E> result = quadTree.query(geometry.getEnvelopeInternal())
        .stream().map(o->entityClass.cast(o));
        return result.filter(e -> e.getGeometry().intersects(geometry));
    }

    @Override
    public void removeAll() {
        all.clear();
        quadTree = new Quadtree();
    }

    @Override
    public Class<E> getMainClass() {
        return entityClass;
    }
}

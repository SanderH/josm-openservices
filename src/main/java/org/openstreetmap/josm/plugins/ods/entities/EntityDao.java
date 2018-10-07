package org.openstreetmap.josm.plugins.ods.entities;

import java.util.stream.Stream;

import com.vividsolutions.jts.geom.Geometry;

public interface EntityDao<E extends Entity> {

    public void insert(E entity);

    public Stream<E> findAll();

    public void removeAll();

    public Stream<E> findByIntersection(Geometry geometry);

    public Class<E> getMainClass();
}

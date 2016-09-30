package org.openstreetmap.josm.plugins.ods.matching;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

public abstract class MatchImpl<E extends Entity> implements Match<E> {
    // TODO rename to key
    private Object id;
    private Class<E> baseType;
    private List<E> osmEntities = new LinkedList<>();
    private List<E> openDataEntities = new LinkedList<>();
    
    @SuppressWarnings("unchecked")
    public MatchImpl(E osmEntity, E openDataEntity, Object key) {
        baseType = (Class<E>) osmEntity.getBaseType();
        this.id = key;
        addOsmEntity(osmEntity);
        addOpenDataEntity(openDataEntity);
        osmEntity.setMatch(this);
        openDataEntity.setMatch(this);
    }

    @Override
    public boolean isSimple() {
        return osmEntities.size() == 1 && openDataEntities.size() == 1;
    }

    @Override
    public boolean isSingleSided() {
        return osmEntities.size() == 0 || openDataEntities.size() == 0;
    }

    @Override
    public E getOsmEntity() {
        if (osmEntities.size() == 0) {
            return null;
        }
        return osmEntities.get(0);
    }

    @Override
    public E getOpenDataEntity() {
        if (openDataEntities.size() == 0) {
            return null;
        }
        return openDataEntities.get(0);
    }

    @Override
    public List<? extends E> getOsmEntities() {
        return osmEntities;
    }

    @Override
    public List<? extends E> getOpenDataEntities() {
        return openDataEntities;
    }

    @Override
    public <E2 extends E>void addOsmEntity(E2 entity) {
        // TODO Do we need a thread safe solution here?
        if (! osmEntities.contains(entity)) {
            osmEntities.add(entity);
            entity.setMatch(this);
        }
    }

    @Override
    public <E2 extends E> void addOpenDataEntity(E2 entity) {
        if (!openDataEntities.contains(entity)) {
            openDataEntities.add(entity);
            entity.setMatch(this);
        }
    }

    @Override
    public void updateMatchTags() {
        ManagedPrimitive osm = getOpenDataEntity().getPrimitive();
        if (osm != null) {
            osm.put(ODS.KEY.BASE, "true");
            osm.put(ODS.KEY.GEOMETRY_MATCH, getGeometryMatch().toString());
            osm.put(ODS.KEY.STATUS_MATCH, getStatusMatch().toString());
            osm.put(ODS.KEY.TAG_MATCH, getAttributeMatch().toString());
            if (getOpenDataEntity().getStatus() == EntityStatus.REMOVAL_DUE) {
                osm.put(ODS.KEY.STATUS, EntityStatus.REMOVAL_DUE.toString());
            }
        }
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Class<E> getBaseType() {
        return baseType;
    }

    @Override
    public MatchStatus getGeometryMatch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MatchStatus getAttributeMatch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MatchStatus getStatusMatch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void analyze() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Match)) {
            return false;
        }
        return (id.equals(((Match<?>)obj).getId()));
    }
}

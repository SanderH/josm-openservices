package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Envelope;

public class ManagedPolygonImpl extends AbstractManagedPrimitive<Relation> implements ManagedPolygon<Relation> {
    private Relation relation;
    private Entity entity;
    private ManagedRing<?> exteriorRing;
    private Collection<ManagedRing<?>> interiorRings;
    private Map<String, String> keys;
    
    public ManagedPolygonImpl(LayerManager layerManager, ManagedRing<?> exteriorRing,
            Collection<ManagedRing<?>> interiorRings,
            Map<String, String> keys) {
        super(layerManager);
        this.exteriorRing = exteriorRing;
        this.interiorRings = (interiorRings != null ? interiorRings : new ArrayList<ManagedRing<?>>(0)); 
        this.keys = keys;
    }

    @Override
    public Envelope getEnvelope() {
       return exteriorRing.getEnvelope();
    }

    @Override
    public BBox getBBox() {
        return exteriorRing.getBBox();
    }

    @Override
    public ManagedRing<?> getExteriorRing() {
        return exteriorRing;
    }
    
    @Override
    public Collection<ManagedRing<?>> getInteriorRings() {
        return interiorRings;
    }
    @Override
    public Relation getPrimitive() {
        return relation;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public Map<String, String> getKeys() {
        return keys;
    }

    @Override
    public Relation create(DataSet dataSet) {
        Relation relation = getPrimitive();
        if (relation == null) {
            List<RelationMember> members = new LinkedList<>();
            ManagedRing<?> outer  = getExteriorRing();
            members.add(new RelationMember("outer", outer.create(dataSet)));
            for (ManagedRing<?> inner : getInteriorRings()) {
                members.add(new RelationMember("inner", inner.create(dataSet)));
            }
            relation = new Relation();
            relation.setKeys(getKeys());
            relation.setMembers(members);
            setPrimitive(relation);
            dataSet.addPrimitive(relation);
        }
        return relation;
    }
    
    
}

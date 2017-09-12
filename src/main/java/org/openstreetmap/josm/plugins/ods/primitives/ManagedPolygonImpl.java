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
import org.openstreetmap.josm.tools.Geometry;

public class ManagedPolygonImpl extends AbstractManagedPrimitive implements ManagedPolygon {
    private Relation relation;
    private final ManagedRing exteriorRing;
    private final Collection<ManagedRing> interiorRings;
    private final Map<String, String> keys;
    private double area;

    public ManagedPolygonImpl(LayerManager layerManager, ManagedRing exteriorRing,
            Collection<ManagedRing> interiorRings,
            Map<String, String> keys) {
        super(layerManager);
        this.exteriorRing = exteriorRing;
        this.interiorRings = (interiorRings != null ? interiorRings : new ArrayList<>(0));
        this.keys = keys;
    }

    @Override
    public BBox getBBox() {
        return exteriorRing.getBBox();
    }

    @Override
    public ManagedRing getExteriorRing() {
        return exteriorRing;
    }

    @Override
    public Collection<ManagedRing> getInteriorRings() {
        return interiorRings;
    }
    @Override
    public Relation getPrimitive() {
        return relation;
    }

    @Override
    public boolean contains(ManagedNode mNode) {
        return Geometry.isNodeInsideMultiPolygon(mNode.getNode(), relation, null);
    }

    @Override
    public Map<String, String> getKeys() {
        return keys;
    }

    @Override
    public Relation create(DataSet dataSet) {
        Relation rel = getPrimitive();
        if (rel == null) {
            List<RelationMember> members = new LinkedList<>();
            ManagedRing outer  = getExteriorRing();
            members.add(new RelationMember("outer", outer.create(dataSet)));
            for (ManagedRing inner : getInteriorRings()) {
                members.add(new RelationMember("inner", inner.create(dataSet)));
            }
            rel = new Relation();
            rel.setKeys(getKeys());
            rel.setMembers(members);
            setPrimitive(rel);
            dataSet.addPrimitive(rel);
        }
        return rel;
    }

    @Override
    public double getArea() {
        if (area == 0) updateArea();
        return area;
    }

    private synchronized void updateArea() {
        area = getExteriorRing().getArea();
        for (ManagedRing ring : getInteriorRings()) {
            area -= ring.getArea();
        }
    }
}

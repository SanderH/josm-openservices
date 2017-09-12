package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.plugins.ods.LayerManager;

/**
 * TODO the relation between this class an managedRelationImpl is crappy.
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class ManagedOgcMultiPolygonImpl extends ManagedRelationImpl implements ManagedOgcMultiPolygon {
    private final Collection<ManagedPolygon> managedPolygons;
    private Double area = null;

    public ManagedOgcMultiPolygonImpl(Collection<ManagedPolygon> managedPolygons,
            Map<String, String> keys) {
        super(getLayerManager(managedPolygons), createRelationMembers(managedPolygons), keys);
        this.managedPolygons = managedPolygons;
    }

    @Override
    public Collection<ManagedPolygon> getPolygons() {
        return managedPolygons;
    }

    @Override
    public Relation create(DataSet dataSet) {
        Relation relation = getPrimitive();
        if (relation == null) {
            List<RelationMember> members = new LinkedList<>();
            for (ManagedPolygon mPolygon : managedPolygons) {
                ManagedRing outer  = mPolygon.getExteriorRing();
                members.add(new RelationMember("outer", outer.create(dataSet)));
                for (ManagedRing inner : mPolygon.getInteriorRings()) {
                    members.add(new RelationMember("inner", inner.create(dataSet)));
                }
            }
            relation = new Relation();
            relation.setKeys(getKeys());
            relation.setMembers(members);
            setPrimitive(relation);
            dataSet.addPrimitive(relation);
        }
        return relation;
    }

    private static List<ManagedRelationMember> createRelationMembers(Collection<ManagedPolygon> managedPolygons) {
        List<ManagedRelationMember> members = new ArrayList<>(managedPolygons.size());
        for (ManagedPolygon polygon : managedPolygons) {
            members.add(new ManagedRelationMemberImpl("", polygon));
        }
        return members;
    }

    private static LayerManager getLayerManager(Collection<ManagedPolygon> managedPolygons) {
        assert !managedPolygons.isEmpty();
        Iterator<ManagedPolygon> it = managedPolygons.iterator();
        LayerManager layerManager = it.next().getLayerManager();
        while (it.hasNext()) {
            assert it.next().getLayerManager() == layerManager;
        }
        return layerManager;
    }

    @Override
    public double getArea() {
        if (area == null) {
            area = 0.0;
            for (ManagedPolygon polygon : managedPolygons) {
                area += polygon.getArea();
            }
        }
        return area;
    }

    //    @Override
    //    protected BBox calculateBBox() {
    //        Iterator<ManagedPolygon> it = managedPolygons.iterator();
    //        BBox bbox = it.next().getBBox();
    //        while (it.hasNext()) {
    //            bbox.add(it.next().getBBox());
    //        }
    //        return bbox;
    //    }
}

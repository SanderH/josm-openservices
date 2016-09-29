package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.plugins.ods.LayerManager;

public class ManagedRelationImpl extends AbstractManagedPrimitive implements ManagedRelation {
    private List<ManagedRelationMember> members;
    private BBox bbox = null;
    
    public ManagedRelationImpl(LayerManager layerManager, List<ManagedRelationMember> members, Map<String, String> keys) {
        super(layerManager, keys);
        this.members = members;
        updateBBox();
    }

    private void updateBBox() {
        this.bbox = calculateBBox();
    }

    @Override
    public BBox getBBox() {
        return bbox;
    }

    @Override
    public Relation getPrimitive() {
        return (Relation) super.getPrimitive();
    }

    @Override
    public Relation create(DataSet dataSet) {
        Relation rel = getPrimitive();
        if (rel == null) {
            rel = new Relation();
            for (ManagedRelationMember member : getMembers()) {
                ManagedPrimitive primitive = member.getPrimitive();
                OsmPrimitive osmPrimitive = primitive.create(dataSet);
                setPrimitive(osmPrimitive);
                rel.addMember(new RelationMember(member.getRole(), osmPrimitive));
            }
            setPrimitive(rel);
        }
        return rel;
    }

    public void setMembers(List<ManagedRelationMember> members) {
        this.members = members;
    }
    
    @Override
    public List<ManagedRelationMember> getMembers() {
        return members;
    }

    @Override
    public List<ManagedNode> getNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ManagedWay> getWays() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ManagedRelation> getRelations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getArea() {
        // In general a relation has no area.
        // Area relations should be implemented as ManagedRing or ManagedPolygon
        return 0;
    }

    protected BBox calculateBBox() {
        Iterator<ManagedRelationMember> it = getMembers().iterator();
        BBox _bbox = it.next().getPrimitive().getBBox();
        while (it.hasNext()) {
            _bbox.add(it.next().getPrimitive().getBBox());
        }
        return _bbox;
    }
}

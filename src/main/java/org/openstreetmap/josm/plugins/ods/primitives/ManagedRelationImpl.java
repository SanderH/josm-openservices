package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationData;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.plugins.ods.LayerManager;

import com.vividsolutions.jts.geom.Envelope;

public class ManagedRelationImpl extends AbstractManagedPrimitive<Relation> implements ManagedRelation {
    private Relation relation;
    private List<ManagedRelationMember> members;
    private long uniqueId = (new RelationData()).getUniqueId();
    private Map<String, String> keys;
    private Envelope envelope = new Envelope();
    private BBox bbox = null;
    
    public ManagedRelationImpl(LayerManager layerManager, List<ManagedRelationMember> members, Map<String, String> keys) {
        super(layerManager);
        this.members = members;
        this.keys = keys;
    }

    @Override
    public Long getUniqueId() {
        return uniqueId;
    }

    @Override
    public Envelope getEnvelope() {
        return envelope;
    }

    @Override
    public BBox getBBox() {
        return bbox;
    }

    @Override
    public Relation getPrimitive() {
        return relation;
    }

    @Override
    public Map<String, String> getKeys() {
        if (relation != null) {
            return relation.getKeys();
        }
        return keys;
    }

    @Override
    public Relation create(DataSet dataSet) {
        Relation relation = getPrimitive();
        if (relation == null) {
            relation = new Relation();
            for (ManagedRelationMember member : getMembers()) {
                ManagedPrimitive<?> primitive = member.getPrimitive();
                OsmPrimitive osmPrimitive = primitive.create(dataSet);
                relation.addMember(new RelationMember(member.getRole(), osmPrimitive));
            }
        }
        return relation;
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
}

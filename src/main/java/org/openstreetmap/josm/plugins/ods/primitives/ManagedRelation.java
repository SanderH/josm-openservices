package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;

import org.openstreetmap.josm.data.osm.Relation;

public interface ManagedRelation extends ManagedPrimitive<Relation> {
    public List<ManagedNode> getNodes();
    public List<ManagedWay> getWays();
    public List<ManagedRelation> getRelations();
    public List<ManagedRelationMember> getMembers();
}

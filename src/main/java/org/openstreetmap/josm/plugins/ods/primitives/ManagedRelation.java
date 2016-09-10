package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;

public interface ManagedRelation extends ManagedPrimitive {
    public List<ManagedNode> getNodes();
    public List<ManagedWay> getWays();
    public List<ManagedRelation> getRelations();
    public List<ManagedRelationMember> getMembers();
}

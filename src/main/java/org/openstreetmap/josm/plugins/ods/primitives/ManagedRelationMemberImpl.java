package org.openstreetmap.josm.plugins.ods.primitives;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class ManagedRelationMemberImpl implements ManagedRelationMember {
    private String role;
    private ManagedPrimitive<? extends OsmPrimitive> primitive;
    
    public ManagedRelationMemberImpl(String role,
            ManagedPrimitive<? extends OsmPrimitive> primitive) {
        super();
        this.role = role;
        this.primitive = primitive;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPrimitive(ManagedPrimitive<OsmPrimitive> primitive) {
        this.primitive = primitive;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public ManagedPrimitive<? extends OsmPrimitive> getPrimitive() {
        return primitive;
    }

}

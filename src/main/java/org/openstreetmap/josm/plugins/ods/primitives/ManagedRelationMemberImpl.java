package org.openstreetmap.josm.plugins.ods.primitives;

public class ManagedRelationMemberImpl implements ManagedRelationMember {
    private String role;
    private ManagedPrimitive primitive;
    
    public ManagedRelationMemberImpl(String role,
            ManagedPrimitive primitive) {
        super();
        this.role = role;
        this.primitive = primitive;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPrimitive(ManagedPrimitive primitive) {
        this.primitive = primitive;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public ManagedPrimitive getPrimitive() {
        return primitive;
    }

}

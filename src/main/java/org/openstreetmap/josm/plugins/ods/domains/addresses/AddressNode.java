package org.openstreetmap.josm.plugins.ods.domains.addresses;

import java.util.Set;
import java.util.function.Function;

import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedNode;
import org.openstreetmap.josm.plugins.ods.storage.IndexKey;
import org.openstreetmap.josm.plugins.ods.storage.IndexKeyImpl;

public interface AddressNode extends Entity<AddressNodeEntityType> {
    public static Function<AddressNode, Object> PC_FULL_HNR_INDEX_FUNCTION = addressNode->{
        Address address = addressNode.getAddress();
        return Address.PC_FULL_HNR_INDEX_FUNCTION.apply(address);
    };
    public static IndexKey<AddressNode> PC_FULL_HNR_INDEX_KEY =
            new IndexKeyImpl<>(AddressNode.class, PC_FULL_HNR_INDEX_FUNCTION);

    void setAddress(Address address);

    public Address getAddress();

    @Override
    public ManagedNode getPrimitive();
    /**
     * Add a building to this address node.
     * An address node is typically contained in exactly 1 building,
     * but there may be exceptions. Therefore there is a possibility to
     * add multiple buildings.
     * @param building
     */
    public void addBuilding(Building building);

    /**
     * Get this address node's building.
     * @return The building to which this address node belongs.
     *     null if there are 0 or more than 1 buildings
     */
    public Building getBuilding();

    /**
     * Get this address node's buildings in case the address is
     * contained in more than 1 building.
     * @return A set of buildings or null if there is not more than 1
     */
    public Set<Building> getBuildings();

    //    public void setGeometry(Point point);
    //
    //    @Override
    //    public Point getGeometry();

}

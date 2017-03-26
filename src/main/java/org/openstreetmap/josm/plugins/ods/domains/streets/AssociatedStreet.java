package org.openstreetmap.josm.plugins.ods.domains.streets;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;

public interface AssociatedStreet {
    public Relation getOsmPrimitive();
    public String getName();
    public Collection<Building> getBuildings();
    public Collection<AddressNode> getAddressNodes();
    public Collection<Street> getStreets();
}

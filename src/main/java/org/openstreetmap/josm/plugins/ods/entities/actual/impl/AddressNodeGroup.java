package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

public class AddressNodeGroup {
    private LatLon coord;
    private List<AddressNode> addressNodes = new ArrayList<>();
    private Building building;
    
    public AddressNodeGroup(AddressNode addressNode) {
        coord = addressNode.getPrimitive().getCenter();
        addressNodes.add(addressNode);
        building = addressNode.getBuilding();
    }
    
    public void addAddressNode(AddressNode node) {
        addressNodes.add(node);
    }
    
    public List<AddressNode> getAddressNodes() {
        return addressNodes;
    }

    public LatLon getCoords() {
        return coord;
    }
    
    public Building getBuilding() {
        return building;
    }
}

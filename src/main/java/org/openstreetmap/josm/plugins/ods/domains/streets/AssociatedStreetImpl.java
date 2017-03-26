package org.openstreetmap.josm.plugins.ods.domains.streets;

import java.util.Collection;
import java.util.LinkedList;

import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;

public class AssociatedStreetImpl implements AssociatedStreet {
    private Relation relation;
    private String name;
    private Collection<Building> buildings = new LinkedList<>();
    private Collection<AddressNode> nodes = new LinkedList<>();
    private Collection<Street> streets = new LinkedList<>();

    
    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public void addNode(AddressNode node) {
        nodes.add(node);
    }

    public void addStreet(Street street) {
        streets.add(street);
    }

    @Override
    public Relation getOsmPrimitive() {
        return relation;
    }

    
    @Override
    public Collection<Building> getBuildings() {
        return buildings;
    }

    @Override
    public Collection<AddressNode> getAddressNodes() {
        return nodes;
    }

    @Override
    public Collection<Street> getStreets() {
        return streets;
    }

}

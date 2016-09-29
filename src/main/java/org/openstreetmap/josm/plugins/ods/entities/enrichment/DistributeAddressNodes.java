package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.HousingUnit;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.AddressNodeGroup;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

/**
 * This enricher finds overlapping nodes in the data and distributes them, so
 * they are no longer overlapping. The MatchAddressToBuildingTask must run
 * before this class, so when can distribute over the line pointing to the
 * centre of the building.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 * 
 */
public class DistributeAddressNodes implements Consumer<Building> {
    private Comparator<? super AddressNode> comparator = new DefaultNodeComparator();

    public DistributeAddressNodes() {
        super();
    }

    public void setComparator(Comparator<? super AddressNode> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void accept(Building building) {
        for (AddressNodeGroup group : buildGroups(building).values()) {
            if (group.getAddressNodes().size() > 1) {
                distribute(group, false);
            }
        }
    }

    /**
     * Analyze all new address nodes and group them by Geometry (Point)
     * 
     * @param newEntities
     */
    private static Map<LatLon, AddressNodeGroup> buildGroups(Building building) {
        Map<LatLon, AddressNodeGroup> groups = new HashMap<>();
        for (HousingUnit housingUnit : building.getHousingUnits()) {
            for (AddressNode addressNode : housingUnit.getAddressNodes()) {
                LatLon coord = addressNode.getPrimitive().getCenter();
                AddressNodeGroup group = groups.get(coord);
                if (group == null) {
                    group = new AddressNodeGroup(addressNode);
                    groups.put(coord, group);
                } else {
                    group.addAddressNode(addressNode);
                }
            }
        }
        return groups;
    }

    private void distribute(AddressNodeGroup group, boolean withUndo) {
        List<AddressNode> nodes = group.getAddressNodes();
        Collections.sort(nodes, comparator);
//        if (group.getBuilding().getGeometry().isEmpty()) {
//            // Happens rarely,
//            // for now return to prevent null pointer Exception
//            return;
//        }
        LatLon center = group.getBuilding().getPrimitive().getCenter();
        double angle = group.getCoords().bearing(center);
        double dx = Math.sin(angle) * 2e-7;
        double dy = Math.cos(angle) * 2e-7;
        double x = group.getCoords().getX();
        double y = group.getCoords().getY();
        for (AddressNode node : nodes) {
            LatLon coord = new LatLon(y, x);
            node.getPrimitive().getNode().setCoor(coord);
            x = x + dx;
            y = y + dy;
        }
    }
    
    private static class DefaultNodeComparator implements Comparator<AddressNode> {
        private Comparator<Address> addressComparator = new DefaultAddressComparator();

        public DefaultNodeComparator() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public int compare(AddressNode o1, AddressNode o2) {
            return addressComparator.compare(o1.getAddress(), o2.getAddress());
        }
    }
    
    private static class DefaultAddressComparator implements Comparator<Address> {

        public DefaultAddressComparator() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public int compare(Address a1, Address a2) {
            int result = Objects.compare(a1.getCityName(), a2.getCityName(), String.CASE_INSENSITIVE_ORDER);
            if (result != 0) return result;
            result = Objects.compare(a1.getPostcode(), a2.getPostcode(), String.CASE_INSENSITIVE_ORDER);
            if (result != 0) return result;
            result = Objects.compare(a1.getStreetName(), a2.getStreetName(), String.CASE_INSENSITIVE_ORDER);
            if (result != 0) return result;
            result = Integer.compare(a1.getHouseNumber(), a2.getHouseNumber());
            if (result != 0) return result;
            if (a1.getHouseLetter() == null || a2.getHouseLetter() == null) {
                if (a2.getHouseNumber() != null) return -1;
                if (a1.getHouseNumber() != null) return 1;
            }
            else {
                result = Character.compare(a1.getHouseLetter(), a2.getHouseLetter());
            }
            if (result != 0) return result;
            return Objects.compare(a1.getHouseNumberExtra(), a2.getHouseNumberExtra(), String.CASE_INSENSITIVE_ORDER);
        }
    }
}

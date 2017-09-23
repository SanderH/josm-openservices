package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.matching.GeometryDifference;
import org.openstreetmap.josm.plugins.ods.matching.StatusDifference;
import org.openstreetmap.josm.plugins.ods.matching.TagDifference;

public class SimpleBagAddressableMatch implements AddressableMatch {
    private final OpenDataAddressNode odAddressNode;
    private final OsmAddressNode osmAddressNode;

    SimpleBagAddressableMatch(OpenDataAddressNode odAddressNode,
            OsmAddressNode osmAddressNode) {
        super();
        this.odAddressNode = odAddressNode;
        this.osmAddressNode = osmAddressNode;
    }

    @Override
    public void clearDifferences() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasDifferences() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StatusDifference getStatusDifference() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setStatusDifference(StatusDifference statusDifference) {
        // TODO Auto-generated method stub

    }

    @Override
    public GeometryDifference getGeometryDifference() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGeometryDifference(GeometryDifference geometryDifference) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<TagDifference> getAttributeDifferences() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addAttributeDifference(TagDifference difference) {
        // TODO Auto-generated method stub

    }
}

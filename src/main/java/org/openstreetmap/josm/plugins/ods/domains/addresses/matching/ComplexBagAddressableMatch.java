package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.matching.GeometryDifference;
import org.openstreetmap.josm.plugins.ods.matching.StatusDifference;
import org.openstreetmap.josm.plugins.ods.matching.TagDifference;

public class ComplexBagAddressableMatch implements AddressableMatch {

    private final Collection<? extends OpenDataAddressNode> odAddressNodes;
    private final Collection<? extends OsmAddressNode> osmAddressNodes;

    ComplexBagAddressableMatch(
            Collection<? extends OpenDataAddressNode> odAddressNodes,
            Collection<? extends OsmAddressNode> osmAddressNodes) {
        this.odAddressNodes = odAddressNodes;
        this.osmAddressNodes = osmAddressNodes;
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

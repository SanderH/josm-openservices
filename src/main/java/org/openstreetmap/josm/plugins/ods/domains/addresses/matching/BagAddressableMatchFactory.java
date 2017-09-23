package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import java.util.Collection;

import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OsmAddressNode;

public class BagAddressableMatchFactory implements AddressableMatchFactory {

    @Override
    public AddressableMatch create(
            OpenDataAddressNode odAddressNode,
            OsmAddressNode osmAddressNode) {

        return new SimpleBagAddressableMatch(odAddressNode, osmAddressNode);
    }

    @Override
    public AddressableMatch create(
            Collection<? extends OpenDataAddressNode> odAddressNodes,
            Collection<? extends OsmAddressNode> osmAddressNodes) {
        return new ComplexBagAddressableMatch(odAddressNodes, osmAddressNodes);
    }
}
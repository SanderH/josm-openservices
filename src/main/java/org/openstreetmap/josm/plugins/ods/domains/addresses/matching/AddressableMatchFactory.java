package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import java.util.Collection;

import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OsmAddressNode;

public interface AddressableMatchFactory {
    public AddressableMatch create(Collection<? extends OpenDataAddressNode> odAddressNodes, Collection<? extends OsmAddressNode> osmAddressNodes);

    public AddressableMatch create(OpenDataAddressNode odAddressNode,
            OsmAddressNode osmAddressNode);
}

package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNodeEntityType;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.StraightMatch;

public class AddressNodeMatch extends StraightMatch<AddressNodeEntityType> {

    public AddressNodeMatch(OsmEntity<AddressNodeEntityType> osmAddressNode, OdEntity<AddressNodeEntityType> odAddressNode) {
        super(osmAddressNode, odAddressNode);
    }
}
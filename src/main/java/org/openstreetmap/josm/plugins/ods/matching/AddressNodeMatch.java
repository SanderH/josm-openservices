package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;

public class AddressNodeMatch extends MatchImpl<AddressNode> {
    Object id;
    private MatchStatus houseNumberMatch;
    private MatchStatus fullHouseNumberMatch;
    private MatchStatus postcodeMatch;
    private MatchStatus streetMatch;
    private MatchStatus cityMatch;

    public AddressNodeMatch(AddressNode an1, AddressNode an2) {
        super(an1, an2);
        this.id = an2.getReferenceId();
        if (an1.getReferenceId() == null) {
            an1.setReferenceId(id);
        }
    }

    public Object getId() {
        return id;
    }

    @Override
    public MatchStatus getGeometryMatch() {
        // If the addressNodes are in the same building, we don't look at
        // their exact location
        return MatchStatus.match(getOsmEntity().getBuilding().getReferenceId(),
                getOpenDataEntity().getBuilding().getReferenceId());
    }

    @Override
    public MatchStatus getStatusMatch() {
        return MatchStatus.match(getOsmEntity().getStatus(), getOpenDataEntity().getStatus());
    }

    @Override
    public MatchStatus getAttributeMatch() {
        return MatchStatus.combine(houseNumberMatch, fullHouseNumberMatch, postcodeMatch,
            streetMatch, cityMatch);
    }

    @Override
    public void analyze() {
        Address a1 = getOsmEntity().getAddress();
        Address a2 = getOpenDataEntity().getAddress();
        houseNumberMatch = MatchStatus.match(a1.getHouseNumber(), a2.getHouseNumber());
        fullHouseNumberMatch = MatchStatus.match(a1.getFullHouseNumber(), a2.getFullHouseNumber());
        postcodeMatch = MatchStatus.match(a1.getPostcode(), a2.getPostcode());
        streetMatch = MatchStatus.match(a1.getStreet(), a2.getStreet());
        cityMatch = MatchStatus.match(a1.getCityName(), a2.getCityName());
    }
}
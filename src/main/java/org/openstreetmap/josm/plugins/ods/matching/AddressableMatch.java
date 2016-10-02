package org.openstreetmap.josm.plugins.ods.matching;

import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.Addressable;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

public class AddressableMatch extends MatchImpl<Addressable> {
    private MatchStatus houseNumberMatch;
    private MatchStatus fullHouseNumberMatch;
    private MatchStatus postcodeMatch;
    private MatchStatus streetMatch;
    private MatchStatus cityMatch;

    public AddressableMatch(Addressable a1, Addressable a2, Object key) {
        super(a1, a2, Addressable.class, key);
    }

    @Override
    public MatchStatus getGeometryMatch() {
        if (this.isSimple()) {
            Building osmBuilding = getOsmEntity().getBuilding();
            Building odBuilding = getOpenDataEntity().getBuilding();
            if (osmBuilding != null && odBuilding != null) {
                if (osmBuilding.getReferenceId() == null) {
                    return UNKNOWN;
                }
            }
            return MatchStatus.match(osmBuilding.getReferenceId(), odBuilding.getReferenceId());
        }
        if (getOpenDataEntity() == null) {
            return UNKNOWN;
        }
        List<? extends Addressable> addressables = getOsmEntities();
        if (addressables == null) {
            return UNKNOWN;
        }
        Set<Object> keys = new HashSet<>();
        for (Addressable addressable : addressables) {
            Buil
            if (address
        }
        if (getOsmEntity().g.g
        if (this.isSimple()) {
            return MatchStatus.match(.getReferenceId(),
                getOpenDataEntity().getBuilding().getReferenceId());
        }
        
    }

    private Set<Object> getBuildingIds(List<? extends Addressable> addressables) {
        
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
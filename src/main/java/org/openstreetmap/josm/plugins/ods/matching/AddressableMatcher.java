package org.openstreetmap.josm.plugins.ods.matching;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Repository;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.Addressable;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

public class AddressableMatcher implements Matcher<Addressable> {
    private OdsModule module;
    
    private Map<Object, Match<Addressable>> addressableMatches = new HashMap<>();
    private List<Addressable> unidentifiedOsmAddressables = new LinkedList<>();
    private List<Addressable> unmatchedOpenDataAddressables = new LinkedList<>();
    private List<Addressable> unmatchedOsmAddressables = new LinkedList<>();

    public AddressableMatcher(OdsModule module) {
        super();
        this.module = module;
    }
    
    @Override
    public void initialize() throws OdsException {
        // Empty implementation. No action required
    }

    
    @Override
    public Class<Addressable> getType() {
        return Addressable.class;
    }

    @Override
    public void run() {
        matchBuildingAddressables();
        matchOtherAddressables();
    }
    
    /**
     * Try to match address nodes for matching buildings
     */
    private void matchBuildingAddressables() {
        Repository repository = module.getOpenDataLayerManager().getRepository();
        for (Building building : repository.getAll(Building.class)) {
            Match<Building> match = building.getMatch(Building.class);
            if (match != null && match.isSimple()) {
                matchAddresses(match);
            }
        }
    }
    
    private void matchAddresses(Match<Building> match) {
        Building odBuilding = match.getOpenDataEntity();
        Building osmBuilding = match.getOsmEntity();
        Map<AddressKey, Addressable> nodes1 = new HashMap<>();
        for (Addressable n1 : odBuilding.getAddressables()) {
            nodes1.put(new AddressKey(n1.getAddress()), n1);
        }
        for (Addressable n2 : osmBuilding.getAddressables()) {
            AddressKey k2 = new AddressKey(n2.getAddress());
            Addressable n1 = nodes1.get(k2);
            if (n1 != null) {
                matchAddressables(n2, n1, k2);
            }
        }
    }

    private void matchAddressables(Addressable an1,
            Addressable an2, Object key) {
        Address a1 = an1.getAddress();
        Address a2 = an2.getAddress();
        if (Objects.equals(a1.getHouseNumber(), a2.getHouseNumber())
                && Objects.equals(a1.getPostcode(), a2.getPostcode())) {
            AddressableMatch match = new AddressableMatch(an1, an2, key);
            match.analyze();
            match.updateMatchTags();
            addressableMatches.put(match.getId(), match);
        }
    }

    private void matchOtherAddressables() {
        // TODO implement
    }

    public void analyze() {
        for (Match<Addressable> match : addressableMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (Addressable addressable: unmatchedOpenDataAddressables) {
            ManagedPrimitive osm = addressable.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, addressable.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        addressableMatches.clear();
        unidentifiedOsmAddressables.clear();
        unmatchedOpenDataAddressables.clear();
        unmatchedOsmAddressables.clear();
    }

    private static class AddressKey {
        private Integer houseNumber;
        private String postcode;
        private Character houseLetter;
        private String houseNumberExtra;
        
        public AddressKey(Address address) {
            this.postcode = normalizePostcode(address.getPostcode());
            this.houseNumber = address.getHouseNumber();
            this.houseLetter = address.getHouseLetter();
            this.houseNumberExtra = address.getHouseNumberExtra();
        }

        private static String normalizePostcode(String postcode) {
            if (postcode == null) {
                return postcode;
            }
            return postcode.replace(" ", "");
        }
        @Override
        public int hashCode() {
            return Objects.hash(postcode, houseNumber, houseLetter, houseNumberExtra);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof AddressKey)) return false;
            AddressKey key = (AddressKey) obj;
            return Objects.equals(houseNumber, key.houseNumber)
                    && Objects.equals(houseLetter, key.houseLetter)
                    && Objects.equals(postcode, key.postcode)
                    && Objects.equals(houseNumberExtra, key.houseNumberExtra);
        }
    }
}

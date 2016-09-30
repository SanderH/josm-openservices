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
    private Repository odRepository;
    private Repository osmRepository;
    private List<Addressable> unidentifiedOsmAddressables = new LinkedList<>();
    private List<Addressable> unmatchedOpenDataAddressables = new LinkedList<>();
    private List<Addressable> unmatchedOsmAddressables = new LinkedList<>();

    public AddressableMatcher(OdsModule module) {
        super();
        this.module = module;
    }
    
    @Override
    public void initialize() throws OdsException {
        odRepository = module.getOpenDataLayerManager().getRepository();
        osmRepository = module.getOsmLayerManager().getRepository();
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
            if (building.getMatch() != null && building.getMatch().isSimple()) {
                matchAddresses(building.getMatch());
            }
        }
    }
    
    private void matchAddresses(BuildingMatch match) {
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
        unmatchedOpenDataAddressables.clear();
        for (Addressable addressable : odRepository.getAll(Addressable.class)) {
            if (addressable.getMatch() == null) {
                unmatchedOpenDataAddressables.add(addressable);
            }
        }
        unmatchedOsmAddressables.clear();
        for (Addressable addressable : osmRepository.getAll(Addressable.class)) {
            if (addressable.getMatch() == null) {
                unmatchedOsmAddressables.add(addressable);
            }
        }
        analyze();
    }

//    private void processOpenDataAddressable(Addressable odAddressable) {
//        Long id = (Long) odAddressable.getReferenceId();
//        Match<Addressable> match = addressableMatches.get(id);
//        if (match != null) {
//            match.addOpenDataEntity(odAddressable);
//            odAddressable.setMatch(match);
//            return;
//        }
//        List<Addressable> osmAddressables = osmAddressableStore.g;
//        if (osmBuildings.size() > 0) {
//            match = new BuildingMatch(osmBuildings.get(0), odBuilding);
//            for (int i=1; i<osmBuildings.size() ; i++) {
//                Building osmBuilding = osmBuildings.get(i);
//                osmBuilding.setMatch(match);
//                match.addOsmEntity(osmBuilding);
//            }
//            buildingMatches.put(id, match);
//        } else {
//            unmatchedOpenDataBuildings.add(odBuilding);
//        }
//    }
//
//    private void processOsmAddressable(Addressable osmAddressable) {
//        Object id = osmBuilding.getReferenceId();
//        if (id == null) {
//            unidentifiedOsmBuildings.add(osmBuilding);
//            return;
//        }
//        Long l;
//        try {
//            l = (Long)id;
//        }
//        catch (@SuppressWarnings("unused") Exception e) {
//            unidentifiedOsmBuildings.add(osmBuilding);
//            return;
//        }
//        List<Building> odBuildings = odBuildingStore.getById(l);
//        if (odBuildings.size() > 0) {
//            Match<Building> match = new BuildingMatch(osmBuilding, odBuildings.get(0));
//            for (int i=1; i<odBuildings.size(); i++) {
//                match.addOpenDataEntity(odBuildings.get(i));
//            }
//            buildingMatches.put(l, match);
//        } else {
//            unmatchedOsmBuildings.add(osmBuilding);
//        }
//    }
    
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
            this.postcode = address.getPostcode();
            this.houseNumber = address.getHouseNumber();
            this.houseLetter = address.getHouseLetter();
            this.houseNumberExtra = address.getHouseNumberExtra();
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
                    && Objects.equals(postcode, key.postcode)
                    && Objects.equals(houseNumberExtra, key.houseNumberExtra);
        }
    }
}

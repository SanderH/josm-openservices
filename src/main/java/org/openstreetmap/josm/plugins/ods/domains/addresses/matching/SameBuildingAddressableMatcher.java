package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Addressable;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.storage.Index;
import org.openstreetmap.josm.plugins.ods.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class SameBuildingAddressableMatcher implements Matcher<Addressable> {
    private final OdsModule module;

    public SameBuildingAddressableMatcher(OdsModule module) {
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
        Repository repository = module.getOpenDataLayerManager().getRepository();
        repository.getAll(Building.class).forEach(building -> {
            Match<Building> match = building.getMatch(Building.class);
            if (match != null && match.isSimple()) {
                matchAddresses(match);
            }
        });
    }

    private static void matchAddresses(Match<Building> match) {
        Building odBuilding = match.getOpenDataEntity();
        Building osmBuilding = match.getOsmEntity();
        Index<Addressable> index = new IndexImpl<>(Addressable.PC_FULL_HNR_INDEX_KEY);
        osmBuilding.getAddressables().forEach(index::insert);
        for (Addressable odAddressable : odBuilding.getAddressables()) {
            index.getAllByTemplate(odAddressable).forEach(osmAddressable -> {
                matchAddressables(osmAddressable, odAddressable);
            });
        }
    }

    private static void matchAddressables(Addressable osmEntity, Addressable odEntity) {
        Match<Addressable> match = osmEntity.getMatch(Addressable.class);
        if (match != null) {
            match.addOpenDataEntity(odEntity);
            odEntity.addMatch(match);
        }
        else {
            match = new AddressableMatch(osmEntity, odEntity);
        }
        match.analyze();
        match.updateMatchTags();
    }

    @Override
    public void reset() {
        //        addressableMatches.clear();
    }
}

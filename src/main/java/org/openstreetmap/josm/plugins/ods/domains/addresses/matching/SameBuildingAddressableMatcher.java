package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.StraightMatch;
import org.openstreetmap.josm.plugins.ods.storage.Index;
import org.openstreetmap.josm.plugins.ods.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class SameBuildingAddressableMatcher implements Matcher {
    private final OdsModule module;

    public SameBuildingAddressableMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    @Override
    public void initialize() throws OdsException {
        // Empty implementation. No action required
    }

    //    @Override
    //    public Class<Addressable> getType() {
    //        return Addressable.class;
    //    }

    @Override
    public void run() {
        Repository repository = module.getOpenDataLayerManager().getRepository();
        repository.getAll(Building.class).forEach(building -> {
            building.getMatch().ifPresent(match-> {
                matchAddresses(building, match);
            });
        });
    }

    private static void matchAddresses(Building odBuilding, Match<Building> match) {
        Building osmBuilding = match.getMainMatch();
        Index<AddressNode> index = new IndexImpl<>(AddressNode.PC_FULL_HNR_INDEX_KEY);
        osmBuilding.getAddressNodes().forEach(index::insert);
        for (AddressNode odAddressNode : odBuilding.getAddressNodes()) {
            index.getAllByTemplate(odAddressNode).forEach(osmAddressNode -> {
                matchAddressNodes(odAddressNode, osmAddressNode);
            });
        }
    }

    private static void matchAddressNodes(AddressNode odEntity, AddressNode osmEntity) {
        if (!osmEntity.getMatch().isPresent()) {
            @SuppressWarnings("unused")
            StraightMatch<AddressNode> match = new StraightMatch<>(osmEntity);
        }
        //        match.analyze();
        //        match.updateMatchTags();
    }

    @Override
    public void reset() {
        //        addressableMatches.clear();
    }
}

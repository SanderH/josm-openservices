package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.matching.Od2OsmMatch;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class SameBuildingAddressableMatcher {
    private final OdsModule module;

    public SameBuildingAddressableMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    public void run() {
        Repository repository = module.getRepository();
        repository.query(OpenDataBuilding.class).forEach(building -> {
            Od2OsmMatch match = building.getMatch();
            if (match != null) {
                matchAddresses(building, match);
            }
        });
    }

    private static void matchAddresses(OpenDataBuilding odBuilding, Od2OsmMatch match) {
        //        if (match instanceof StraightMatch<?>) {
        //            StraightMatch<BuildingEntityType> straightMatch = (StraightMatch<BuildingEntityType>) match;
        //            OsmBuilding osmBuilding = (OsmBuilding) straightMatch.getOsmEntity();
        //            Index<OsmAddressNode> index = new IndexImpl<>(OsmAddressNode.class, AddressNode.PC_FULL_HNR_INDEX_KEY);
        //            osmBuilding.getAddressNodes().forEach(index::insert);
        //            for (OpenDataAddressNode odAddressNode : odBuilding.getAddressNodes()) {
        //                Object id = AddressNode.PC_FULL_HNR_INDEX_FUNCTION.apply(odAddressNode);
        //                index.getAll(id).forEach(osmAddressNode -> {
        //                    matchAddressNodes(odAddressNode, osmAddressNode);
        //                });
        //            }
        //        }
    }

    private static void matchAddressNodes(OpenDataAddressNode odEntity, OsmAddressNode osmEntity) {
        //        Od2OsmMatch<AddressNodeEntityType> match = odEntity.getMatch();
        //        if (match != null && osmEntity.getMatch() == match) {
        //            return;
        //        }
        //        if (match == null && osmEntity.getMatch() == null) {
        //            @SuppressWarnings("unused")
        //            StraightMatch<AddressNodeEntityType> straightMatch = new StraightMatch<>(osmEntity, odEntity);
        //        }
    }
}

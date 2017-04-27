package org.openstreetmap.josm.plugins.ods.domains.addresses.processing;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.HousingUnit;
import org.openstreetmap.josm.plugins.ods.io.OdsProcessor;
import org.openstreetmap.josm.plugins.ods.storage.Repository;


/**
 * <p>Try to find a matching building for every HousingUnit passed to the AddressNode
 * consumer. The buildingRef of the address node will be used to do the matching.</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If the buidingRef is null, or no building with this referenceId was found,
 * this must be an error in the integrity of the opendata object. The faulty addressNode will
 * be forwarded to the unmatchedHousingUnit consumer if available;
 *
 * @author gertjan
 *
 */
public class HousingUnitToBuildingConnector implements OdsProcessor {
    private final OdsModule module;
    private Consumer<HousingUnit> unmatchedHousingUnitHandler;

    public HousingUnitToBuildingConnector() {
        super();
        this.module = OdsProcessor.getModule();
    }

    public void setUnmatchedHousingUnitHandler(
            Consumer<HousingUnit> unmatchedHousingUnitHandler) {
        this.unmatchedHousingUnitHandler = unmatchedHousingUnitHandler;
    }

    @Override
    public void run() {
        LayerManager layerManager = module.getOpenDataLayerManager();
        layerManager.getRepository().getAll(HousingUnit.class)
        .forEach(this::matchHousingUnitToBuilding);
    }

    /**
     * Find a matching building for a housing unit.
     *
     * @param housingUnit
     */
    public void matchHousingUnitToBuilding(HousingUnit housingUnit) {
        Repository repository = module.getOpenDataLayerManager().getRepository();
        if (housingUnit.getBuilding() == null) {
            Object buildingRef = housingUnit.getBuildingRef();
            if (buildingRef != null) {
                repository.query(Building.class, "referenceId", buildingRef)
                .forEach(building -> {
                    housingUnit.setBuilding(building);
                    building.addHousingUnit(housingUnit);
                });
                if (housingUnit.getBuilding() == null) {
                    reportUnmatched(housingUnit);
                }
            }
            else {
                reportUnmatched(housingUnit);
            }
        }
    }

    private void reportUnmatched(HousingUnit housingUnit) {
        if (unmatchedHousingUnitHandler != null) {
            unmatchedHousingUnitHandler.accept(housingUnit);
        }
    }
}

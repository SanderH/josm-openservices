package org.openstreetmap.josm.plugins.ods.domains.addresses.processing;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.io.OdsProcessor;
import org.openstreetmap.josm.plugins.ods.storage.Repository;


/**
 * <p>Try to find a matching building for every BuildingUnit passed to the AddressNode
 * consumer. The buildingRef of the address node will be used to do the matching.</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If the buidingRef is null, or no building with this referenceId was found,
 * this must be an error in the integrity of the opendata object. The faulty addressNode will
 * be forwarded to the unmatchedBuildingUnit consumer if available;
 *
 * @author gertjan
 *
 */
public class BuildingUnitToBuildingConnector implements OdsProcessor {
    private final OdsModule module;
    private Consumer<BuildingUnit> unmatchedBuildingUnitHandler;

    public BuildingUnitToBuildingConnector() {
        super();
        this.module = OdsProcessor.getModule();
    }

    public void setUnmatchedBuildingUnitHandler(
            Consumer<BuildingUnit> unmatchedBuildingUnitHandler) {
        this.unmatchedBuildingUnitHandler = unmatchedBuildingUnitHandler;
    }

    @Override
    public void run() {
        module.getRepository().getAll(BuildingUnit.class)
        .forEach(this::matchBuildingUnitToBuilding);
    }

    /**
     * Find a matching building for a housing unit.
     *
     * @param buildingUnit
     */
    public void matchBuildingUnitToBuilding(BuildingUnit buildingUnit) {
        Repository repository = module.getRepository();
        if (buildingUnit.getBuilding() == null) {
            Object buildingRef = buildingUnit.getBuildingRef();
            if (buildingRef != null) {
                repository.query(OpenDataBuilding.class, "referenceId", buildingRef)
                .forEach(building -> {
                    buildingUnit.setBuilding(building);
                    building.addBuildingUnit(buildingUnit);
                });
                if (buildingUnit.getBuilding() == null) {
                    reportUnmatched(buildingUnit);
                }
            }
            else {
                reportUnmatched(buildingUnit);
            }
        }
    }

    private void reportUnmatched(BuildingUnit buildingUnit) {
        if (unmatchedBuildingUnitHandler != null) {
            unmatchedBuildingUnitHandler.accept(buildingUnit);
        }
    }
}

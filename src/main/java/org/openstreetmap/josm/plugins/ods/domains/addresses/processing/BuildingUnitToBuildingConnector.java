package org.openstreetmap.josm.plugins.ods.domains.addresses.processing;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.io.AbstractTask;
import org.openstreetmap.josm.plugins.ods.io.Task;
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
public class BuildingUnitToBuildingConnector extends AbstractTask {
    private static List<Class<? extends Task>> dependencies =
            Arrays.asList();
    private final OdsModule module = OpenDataServicesPlugin.getModule();
    private Consumer<BuildingUnit> unmatchedBuildingUnitHandler;

    public BuildingUnitToBuildingConnector() {
        super();
    }

    @Override
    public Collection<Class<? extends Task>> getDependencies() {
        return dependencies;
    }


    public void setUnmatchedBuildingUnitHandler(
            Consumer<BuildingUnit> unmatchedBuildingUnitHandler) {
        this.unmatchedBuildingUnitHandler = unmatchedBuildingUnitHandler;
    }

    @Override
    public Void call() {
        module.getRepository().query(BuildingUnit.class)
        .forEach(this::matchBuildingUnitToBuilding);
        return null;
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

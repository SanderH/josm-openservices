package org.openstreetmap.josm.plugins.ods.domains.addresses.processing;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.io.AbstractTask;


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
    private final EntityDao<OpenDataBuildingUnit> buildingUnitDao;
    private final EntityDao<OpenDataBuilding> buildingDao;
    private Consumer<BuildingUnit> unmatchedBuildingUnitHandler;

    public BuildingUnitToBuildingConnector(
            EntityDao<OpenDataBuildingUnit> buildingUnitDao,
            EntityDao<OpenDataBuilding> buildingDao) {
        super();
        this.buildingUnitDao = buildingUnitDao;
        this.buildingDao = buildingDao;
    }

    public void setUnmatchedBuildingUnitHandler(
            Consumer<BuildingUnit> unmatchedBuildingUnitHandler) {
        this.unmatchedBuildingUnitHandler = unmatchedBuildingUnitHandler;
    }

    @Override
    public Void call() {
        Map<Object, OpenDataBuilding> buildingMap =
                buildingDao.findAll().collect(Collectors.toMap(OpenDataBuilding::getReferenceId, Function.identity()));
        buildingUnitDao.findAll().forEach(buildingUnit -> {
            matchBuildingUnitToBuilding(buildingMap, buildingUnit);
        });
        return null;
    }

    /**
     * Find a matching building for a housing unit.
     *
     * @param buildingUnit
     */
    public void matchBuildingUnitToBuilding(Map<Object, OpenDataBuilding> buildingMap,
            BuildingUnit buildingUnit) {
        if (buildingUnit.getBuilding() == null) {
            Object buildingRef = buildingUnit.getBuildingRef();
            if (buildingRef != null) {
                OpenDataBuilding building = buildingMap.get(buildingRef);
                if (buildingUnit.getBuilding() != null) {
                    buildingUnit.setBuilding(building);
                    building.addBuildingUnit(buildingUnit);
                }
                else {
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

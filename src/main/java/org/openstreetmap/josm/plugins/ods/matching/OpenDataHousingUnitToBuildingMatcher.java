package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Iterator;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.EntityRepository;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.HousingUnit;


/**
 * <p>Try to find a matching building for every AddressNode passed to the AddressNode
 * consumer. The referenceId of the address node will be used to do the matching.</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If the referenceId is null, or no building with this referenceId was found,
 * this must be an error in the integrity of the opendata object. The faulty addressNode will
 * be forwarded to the unmatchedAddressNode consumer if available;
 * 
 * @author gertjan
 *
 */
public class OpenDataHousingUnitToBuildingMatcher {
    private OdsModule module;
    private Consumer<HousingUnit> unmatchedHousingUnitHandler;
    
    public OpenDataHousingUnitToBuildingMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    public void setUnmatchedHousingUnitHandler(
            Consumer<HousingUnit> unmatchedHousingUnitHandler) {
        this.unmatchedHousingUnitHandler = unmatchedHousingUnitHandler;
    }

    /**
     * Find a matching building for a housing unit.
     * 
     * @param housingUnit
     */
    public void matchHousingUnitToBuilding(HousingUnit housingUnit) {
//        OpenDataBuildingStore buildings = (OpenDataBuildingStore) module
//                .getOpenDataLayerManager().getEntityStore(Building.class);
//        if (housingUnit.getBuilding() == null) {
//            Object buildingRef = housingUnit.getBuildingRef();
//            if (buildingRef != null) {
//                List<Building> matchedbuildings = buildings.getById(buildingRef);
//                if (matchedbuildings.size() == 1) {
//                    Building building = matchedbuildings.get(0);
//                    housingUnit.setBuilding(building);
//                    building.addHousingUnit(housingUnit);
//                }
//                else {
//                    reportUnmatched(housingUnit);
//                }
//            }
//            else {
//                reportUnmatched(housingUnit);
//            }
//        }
        EntityRepository repository = module.getOpenDataLayerManager().getRepository();
        if (housingUnit.getBuilding() == null) {
            Object buildingRef = housingUnit.getBuildingRef();
            if (buildingRef != null) {
                Iterator<Building> matchedbuildings = repository.query(Building.class, "referenceId", buildingRef).iterator();
                if (matchedbuildings.hasNext()) {
                    Building building = matchedbuildings.next();
                    housingUnit.setBuilding(building);
                    building.addHousingUnit(housingUnit);
                }
                else {
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

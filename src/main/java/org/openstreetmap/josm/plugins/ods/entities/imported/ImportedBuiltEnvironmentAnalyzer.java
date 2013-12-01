package org.openstreetmap.josm.plugins.ods.entities.imported;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuiltEnvironmentEntitySet;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Street;

/**
 * The ImportedBuiltEnvironmentAnalyzer analyzes buildings, addresses and related
 * objects like streets and cities.
 * 
 * @author gertjan
 * 
 */
public class ImportedBuiltEnvironmentAnalyzer implements ImportedEntityAnalyzer {
    private BuiltEnvironmentEntitySet entitySet;

    /**
     * 
     */
    @Override
    public void setEntitySet(EntitySet entitySet) {
        this.entitySet = new BuiltEnvironmentEntitySet(entitySet);
    }

    public BuiltEnvironmentEntitySet getEntitySet() {
        return entitySet;
    }

    @Override
    public void analyzeNewEntities(Collection<Entity> entities, Bounds bounds) {
        List<ImportedBuilding> newBuildings = new LinkedList<ImportedBuilding>();
        List<ImportedAddress> newAddresses = new LinkedList<ImportedAddress>();

        for (Entity entity : entities) {
            if (entity instanceof ImportedBuilding) {
                newBuildings.add((ImportedBuilding) entity);
            } else if (entity instanceof ImportedAddress) {
                newAddresses.add((ImportedAddress) entity);
            }
        }
        analyzeBuildingCompleteness(newBuildings);
        analyzeAddressStreets(newAddresses);
        analyzeAddressBuildings(newAddresses);
    }

    protected void analyzeAddressStreets(List<ImportedAddress> newAddresses) {
        for (ImportedAddress address : newAddresses) {
            String fullStreetName = ImportedStreet.getFullName(
                address.getPlaceName(), address.getStreetName());
            if (fullStreetName != null) {
                Street street = getEntitySet().getStreet(fullStreetName);
                if (street == null) {
                    street = new ImportedStreet(address.getPlaceName(), address.getStreetName());
                    getEntitySet().add(street);
                }
                address.setStreet(street);
                street.getAddresses().add(address);
            }
        }
    }

    protected void analyzeAddressBuildings(List<ImportedAddress> newAddresses) {
        for (ImportedAddress address : newAddresses) {
            assert address.getBuilding() == null;
            Serializable buildingRef = address.getBuildingRef();
            if (buildingRef != null) {
                analyzeAddressBuildingByRef(address);
            }
            else {
                analyzeAddressBuildingByGeometry(address);
            }
        }
    }

    /**
     * Use the building reference in the address to find the building to
     * which this address belongs
     * 
     * @param address
     */
    private void analyzeAddressBuildingByRef(ImportedAddress address) {
        Serializable buildingRef = address.getBuildingRef();
        Building building = entitySet.getBuildings().get(buildingRef);
        // TODO create issue if the building is not found
        if (building != null) {
            address.setBuilding(building);
            building.getAddresses().add(address);
        }
    }

    /**
     * Use the geometry (point) of this address to find the building to
     * which this address belongs
     * 
     * @param address
     */
    private void analyzeAddressBuildingByGeometry(ImportedAddress address) {
        Iterator<Building> iterator = entitySet.getBuildings().iterator();
        boolean found = false;
        while (iterator.hasNext() && !found) {
            Building building = iterator.next();
            if (building.getGeometry().covers(address.getGeometry())) {
                address.setBuilding(building);
                building.getAddresses().add(address);
                found = true;
            }
        }
    }

    protected void analyzeBuildingCompleteness(List<ImportedBuilding> newBuildings) {
        for (ImportedBuilding building : newBuildings) {
            building.setComplete(entitySet.getBoundary().covers(building.getGeometry()));
        }
    }

    protected void analyzeAddress(ImportedAddress building) {
        // TODO Auto-generated method stub

    }

}

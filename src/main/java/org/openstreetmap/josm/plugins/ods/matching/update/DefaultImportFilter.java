package org.openstreetmap.josm.plugins.ods.matching.update;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

/**
 * Default import filter implementation
 * TODO This implementation contains code that is specific for address nodes.
 * That part should be move to a place that is specific for address nodes
 *
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class DefaultImportFilter implements ImportFilter {

    @Override
    public boolean test(OdEntity entity) {
        boolean doImport = false;
        switch (entity.getStatus()) {
        case IN_USE:
        case IN_USE_NOT_MEASURED:
        case RECONSTRUCTION:
        case CONSTRUCTION:
            doImport = true;
            
            // Don't import addresses belonging to buildings to be removed
            if (entity instanceof OdAddressNode) {
                OdAddressNode addressNode = (OdAddressNode) entity;
                OdBuilding building = addressNode.getBuilding();
                if (building != null && building.getStatus().equals(EntityStatus.REMOVAL_DUE)) {
                    doImport = false;
                }
            }
            break;
        case PLANNED:
            // Import planned addresses if the building is under construction
            if (entity instanceof OdAddressNode) {
                OdAddressNode addressNode = (OdAddressNode) entity;
                OdBuilding building = addressNode.getBuilding();
                if (building != null && building.getStatus().equals(EntityStatus.CONSTRUCTION)) {
                    doImport = true;
                }
            }
            break;
        case REMOVAL_DUE:
            // Don't import buildings that are to be removed
            doImport = false;
            break;
        default:
            doImport = false;
        }
        return doImport;
    }

}

package org.openstreetmap.josm.plugins.ods.update;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

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
    public boolean test(Entity entity) {
        switch (entity.getStatus()) {
        case NOT_REALIZED:
        case REMOVED:
        case UNKNOWN:
        case PLANNED:
            return false;
        case CONSTRUCTION:
            if (entity instanceof AddressNode) {
                AddressNode addressNode = (AddressNode) entity;
                Building building = addressNode.getBuilding();
                if (building != null && building.getStatus().equals(EntityStatus.PLANNED)) {
                    return false;
                }
            }
            return true;
        case IN_USE:
        case IN_USE_NOT_MEASURED:
            // TODO Check age
            return true;
            
        default:
            return false;
        }
    }

}

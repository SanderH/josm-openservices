package org.openstreetmap.josm.plugins.ods.domains.buildings.processing;

import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.io.AbstractTask;

/**
 * Extract Address node Entities from building units and merge them
 *  into the main repository of this module.
 *
 * @author Gertjan Idema
 *
 */
public class AddressNodeEntitiyExtractor extends AbstractTask {
    private final EntityDao<OpenDataBuildingUnit> buildingUnitDao;
    private final EntityDao<OpenDataAddressNode> addressNodeDao;

    public AddressNodeEntitiyExtractor(
            EntityDao<OpenDataBuildingUnit> buildingUnitDao,
            EntityDao<OpenDataAddressNode> addressNodeDao) {
        super();
        this.buildingUnitDao = buildingUnitDao;
        this.addressNodeDao = addressNodeDao;
    }

    @Override
    public Void call() throws Exception {
        buildingUnitDao.findAll().forEach( bu -> {
            addressNodeDao.insert(bu.getMainAddressNode());
        });
        return null;
    }
}
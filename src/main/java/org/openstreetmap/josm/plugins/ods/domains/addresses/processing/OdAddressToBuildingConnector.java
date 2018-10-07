package org.openstreetmap.josm.plugins.ods.domains.addresses.processing;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.io.AbstractTask;

import com.vividsolutions.jts.geom.Geometry;


/**
 * <p>Try to find a matching building for every AddressNode passed to the AddressNode
 * consumer. The geometry of the address node will be used to do the matching.</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If no matching building was found,the faulty addressNode will
 * be forwarded to the unmatchedAddressNode consumer if available;
 *
 * @author gertjan
 *
 */
public class OdAddressToBuildingConnector extends AbstractTask {
    private Consumer<AddressNode> unmatchedAddressNodeHandler = (t -> {/**/});
    private final EntityDao<OpenDataAddressNode> addressNodeDao;
    private final EntityDao<OpenDataBuilding> buildingDao;

    public OdAddressToBuildingConnector(
            EntityDao<OpenDataAddressNode> addressNodeDao,
            EntityDao<OpenDataBuilding> buildingDao) {
        super();
        this.addressNodeDao = addressNodeDao;
        this.buildingDao = buildingDao;
    }

    public void setUnmatchedHousingUnitHandler(
            Consumer<AddressNode> handler) {
        this.unmatchedAddressNodeHandler = handler;
    }

    @Override
    public Void call() {
        addressNodeDao.findAll()
        .forEach(this::match);
        return null;
    }

    /**
     * Find a matching building for an address node.
     *
     * @param addressNode
     */
    public void match(OpenDataAddressNode addressNode) {
        if (addressNode.getBuilding() == null) {
            Geometry geometry = addressNode.getGeometry();
            List<OpenDataBuilding> matches = buildingDao.findByIntersection(geometry).collect(Collectors.toList());
            if (matches.size() == 0) {
                reportUnmatched(addressNode);
            }
            else if (matches.size() == 1) {
                OpenDataBuilding building = matches.get(0);
                addressNode.addBuilding(building);
                building.getAddressNodes().add(addressNode);
            }
            else {
                throw new UnsupportedOperationException();
            }
        }
    }

    private void reportUnmatched(AddressNode addressNode) {
        unmatchedAddressNodeHandler.accept(addressNode);
    }
}

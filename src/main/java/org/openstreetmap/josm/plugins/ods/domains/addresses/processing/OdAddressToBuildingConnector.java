package org.openstreetmap.josm.plugins.ods.domains.addresses.processing;

import java.util.Iterator;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.io.AbstractTask;
import org.openstreetmap.josm.plugins.ods.storage.GeoRepository;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

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
    private final OdsModule module = OpenDataServicesPlugin.getModule();
    private Consumer<AddressNode> unmatchedAddressNodeHandler = (t -> {/**/});

    public OdAddressToBuildingConnector() {
        super();
    }

    public void setUnmatchedHousingUnitHandler(
            Consumer<AddressNode> handler) {
        this.unmatchedAddressNodeHandler = handler;
    }

    @Override
    public Void call() {
        module.getRepository().query(OpenDataAddressNode.class)
        .forEach(this::match);
        return null;
    }

    /**
     * Find a matching building for an address node.
     *
     * @param addressNode
     */
    public void match(OpenDataAddressNode addressNode) {
        Repository repository = module.getRepository();
        if (addressNode.getBuilding() == null) {
            Geometry geometry = addressNode.getGeometry();
            if (geometry != null && repository instanceof GeoRepository) {
                Iterator<OpenDataBuilding> matchedbuildings = ((GeoRepository)repository).queryIntersection(OpenDataBuilding.class, "geometry", geometry).iterator();
                if (matchedbuildings.hasNext()) {
                    OpenDataBuilding building = matchedbuildings.next();
                    addressNode.addBuilding(building);
                    building.getAddressNodes().add(addressNode);
                }
                else {
                    reportUnmatched(addressNode);
                }
            }
            else {
                reportUnmatched(addressNode);
            }
        }
    }

    private void reportUnmatched(AddressNode addressNode) {
        unmatchedAddressNodeHandler.accept(addressNode);
    }
}

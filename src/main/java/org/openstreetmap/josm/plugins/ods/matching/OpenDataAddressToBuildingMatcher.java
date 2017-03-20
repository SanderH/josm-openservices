package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Iterator;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.GeoRepository;
import org.openstreetmap.josm.plugins.ods.entities.Repository;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

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
public class OpenDataAddressToBuildingMatcher {
    private OdsModule module;
    private Consumer<AddressNode> unmatchedAddressNodeHandler;
    
    public OpenDataAddressToBuildingMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    public void setUnmatchedHousingUnitHandler(
            Consumer<AddressNode> unmatchedAddressNodeHandler) {
        this.unmatchedAddressNodeHandler = unmatchedAddressNodeHandler;
    }

    /**
     * Find a matching building for an address node.
     * 
     * @param addressNode
     */
    public void match(AddressNode addressNode) {
        Repository repository = module.getOpenDataLayerManager().getRepository();
        if (addressNode.getBuilding() == null) {
            Geometry geometry = addressNode.getGeometry();
            if (geometry != null && repository instanceof GeoRepository) {
                Iterator<Building> matchedbuildings = ((GeoRepository)repository).queryIntersection(Building.class, "geometry", geometry).iterator();
                if (matchedbuildings.hasNext()) {
                    Building building = matchedbuildings.next();
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
        if (unmatchedAddressNodeHandler != null) {
            unmatchedAddressNodeHandler.accept(addressNode);
        }
    }
}

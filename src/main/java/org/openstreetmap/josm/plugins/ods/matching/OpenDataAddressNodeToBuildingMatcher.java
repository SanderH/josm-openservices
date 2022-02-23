package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OpenDataBuildingStore;
import org.openstreetmap.josm.tools.Logging;


/**
 * <p>Try to find a matching building for every OdAddressNode passed to the OdAddressNode
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
public class OpenDataAddressNodeToBuildingMatcher {
    private final OdsModule module;
    private Consumer<OdAddressNode> unmatchedAddressNodeHandler;

    public OpenDataAddressNodeToBuildingMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    public void setUnmatchedAddressNodeHandler(
            Consumer<OdAddressNode> unmatchedAddressNodeHandler) {
        this.unmatchedAddressNodeHandler = unmatchedAddressNodeHandler;
    }

    /**
     * Find a matching building for an address.
     *
     * @param addressNode
     */
    public void matchAddressToBuilding(OdAddressNode addressNode) {
        OpenDataBuildingStore buildings = (OpenDataBuildingStore) module
                .getOpenDataLayerManager().getEntityStore(OdBuilding.class);
        if (addressNode.getBuilding() == null) {
            Object buildingRef = addressNode.getBuildingRef();
            if (buildingRef != null) {
                List<OdBuilding> matchedbuildings = buildings.getById(buildingRef);
                if (matchedbuildings.size() == 1) {
                    OdBuilding building = matchedbuildings.get(0);
                    if (building.getGeometry().contains(addressNode.getGeometry()))
                    {
                        addressNode.setBuilding(building);
                        building.getAddressNodes().add(addressNode);
                    }
                    else
                    {
                        Logging.debug("Address node {0} is not within building {1} looking for building containing the address", addressNode.getReferenceId(), buildingRef);
                        // associated building doesn't contain the address node
                        // probably address is associated with multiple buildings and WFS returned 'wrong' one
                        // find the building where the address node is located
                        for(OdBuilding loopBuilding : buildings)
                        {
                            if (loopBuilding.getGeometry().contains(addressNode.getGeometry()))
                            {
                                Logging.debug("Found address node {0} replacement building {1}", addressNode.getReferenceId(), loopBuilding.getReferenceId());
                                addressNode.setReferenceId(loopBuilding.getReferenceId());
                                addressNode.setBuilding(loopBuilding);
                                loopBuilding.getAddressNodes().add(addressNode);
                                break;
                            }
                        }
                    }
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

    private void reportUnmatched(OdAddressNode addressNode) {
        if (unmatchedAddressNodeHandler != null) {
            unmatchedAddressNodeHandler.accept(addressNode);
        }
    }
}

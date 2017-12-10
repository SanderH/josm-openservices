package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import static org.openstreetmap.josm.plugins.ods.storage.query.Query.ATTR;
import static org.openstreetmap.josm.plugins.ods.storage.query.Query.EQUALS;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.storage.Repository;
import org.openstreetmap.josm.plugins.ods.storage.query.Query;

public class PcHnrAddressableMatcher implements Matcher {
    private final AddressableMatchFactory matchFactory = new BagAddressableMatchFactory();
    private final OdsModule module;

    public PcHnrAddressableMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    @Override
    public void initialize() throws OdsException {
        // Empty implementation. No action required
    }

    @Override
    public void run() {
        Repository repository = module.getRepository();
        repository.query(OpenDataAddressNode.class).forEach(odAddressNode -> {
            String postcode = odAddressNode.getAddress().getPostcode();
            String fullHouseNumber = odAddressNode.getAddress().getFullHouseNumber();
            if (postcode != null && fullHouseNumber != null) {
                Query<OsmAddressNode> query = repository.query(OsmAddressNode.class,
                        EQUALS(ATTR("address.postcode"), postcode).AND(EQUALS(ATTR("address.fullHouseNumber"), fullHouseNumber)));
                List<? extends OsmAddressNode> osmAddressNodes = query.toList();
                createMatch(odAddressNode, osmAddressNodes);
            }
        });
    }

    private void createMatch(OpenDataAddressNode odAddressNode, List<? extends OsmAddressNode> osmAddressNodes) {
        if (osmAddressNodes.isEmpty()) return;
        if (osmAddressNodes.size() == 1) {
            OsmAddressNode osmAddressNode = osmAddressNodes.get(0);
            Match match = matchFactory.create(odAddressNode, osmAddressNodes.get(0));
            odAddressNode.setMatch(match);
            osmAddressNode.setMatch(match);
            checkStreet(odAddressNode, osmAddressNode);
        }
        else {
            Match match = matchFactory.create(Collections.singletonList(odAddressNode), osmAddressNodes);
            odAddressNode.setMatch(match);
            osmAddressNodes.forEach(n -> n.setMatch(match));
        }
    }

    private static void checkStreet(OpenDataAddressNode odAddressNode, OsmAddressNode osmAddressNode) {
        Address odAddress = odAddressNode.getAddress();
        Address osmAddress = osmAddressNode.getAddress();
        if (Objects.equals(odAddress.getStreetName(), osmAddress.getStreetName())) {
            return;
        }
    }

    @Override
    public void reset() {
        //        addressableMatches.clear();
    }
}

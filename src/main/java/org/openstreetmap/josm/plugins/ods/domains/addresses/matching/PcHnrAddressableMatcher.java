package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public class PcHnrAddressableMatcher {
    private final AddressableMatchFactory matchFactory = new BagAddressableMatchFactory();
    private final EntityDao<OpenDataAddressNode> odAddressNodeDao;
    private final EntityDao<OsmAddressNode> osmAddressNodeDao;

    public PcHnrAddressableMatcher(
            EntityDao<OpenDataAddressNode> odAddressNodeDao,
            EntityDao<OsmAddressNode> osmAddressNodeDao) {
        super();
        this.odAddressNodeDao = odAddressNodeDao;
        this.osmAddressNodeDao = osmAddressNodeDao;
    }

    public void run() {
        Map<PcHnrKey, List<OsmAddressNode>> index = new HashMap<>();
        osmAddressNodeDao.findAll().forEach(an -> {
            PcHnrKey key = new PcHnrKey(an);
            index.computeIfAbsent(key, l -> new ArrayList<>()).add(an);
        });
        odAddressNodeDao.findAll().forEach(odAddressNode -> {
            PcHnrKey key = new PcHnrKey(odAddressNode);
            List<? extends OsmAddressNode> anList = index.get(key);
            createMatch(odAddressNode, anList);
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

    private static class PcHnrKey {
        private final String postcode;
        private final Integer houseNumber;

        PcHnrKey(AddressNode addressNode) {
            this.postcode = addressNode.getAddress().getPostcode();
            this.houseNumber = addressNode.getAddress().getHouseNumber();
        }

        @Override
        public int hashCode() {
            return Objects.hash(postcode, houseNumber);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PcHnrKey)) return false;
            PcHnrKey other = (PcHnrKey) obj;
            return Objects.equals(postcode,  other.postcode) &&
                    Objects.equals(houseNumber, other.houseNumber);
        }
    }
}

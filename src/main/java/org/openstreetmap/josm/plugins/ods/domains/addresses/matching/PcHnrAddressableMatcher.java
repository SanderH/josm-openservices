package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.storage.Index;
import org.openstreetmap.josm.plugins.ods.storage.Repository;
import org.openstreetmap.josm.tools.Logging;

public class PcHnrAddressableMatcher implements Matcher {
    private final OdsModule module;

    //    private final Map<Object, Match<Addressable>> addressableMatches = new HashMap<>();

    public PcHnrAddressableMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    @Override
    public void initialize() throws OdsException {
        // Empty implementation. No action required
    }

    //
    //    @Override
    //    public Class<Addressable> getType() {
    //        return Addressable.class;
    //    }
    //
    @Override
    public void run() {
        Repository repository = module.getRepository();
        Index<AddressNode> pcHnrIndex = repository.getIndex(AddressNode.PC_FULL_HNR_INDEX_KEY);
        //        odRepository.getAll(Addressable.class)
        //        .filter(a -> a.getMatch(Addressable.class) == null)
        //        .filter(a -> a.getAddress() != null)
        //        .forEach(a -> findMatch(pcHnrIndex, a));
        repository.iterator(OpenDataAddressNode.class).forEachRemaining(addressNode -> {
            //            if (addressNode.getMatch() == null) {
            //                findMatch(pcHnrIndex, addressNode);
            //            }
        });
    }

    private static void findMatch(Index<AddressNode> index, OpenDataAddressNode odAddressNode) {
        OsmAddressNode[] nodes = index.getAllByTemplate(odAddressNode).toArray(OsmAddressNode[]::new);
        if (nodes.length == 0) return;
        if (nodes.length == 1) {
            createMatch(odAddressNode, nodes[0]);
        }
        else {
            Logging.warn("Duplicate match");
        }
    }

    private static void createMatch(OpenDataAddressNode odAddressNode, OsmAddressNode osmAddressNode) {
        //        Match match = osmAddressNode.getMatch();
        //        if (match == null) {
        //            match = new StraightMatch<>(osmAddressNode, odAddressNode);
        //            odAddressNode.setMatch(match);
        //            return;
        //        }
        Logging.warn("Complex match");
        //        match.analyze();
        //        match.updateMatchTags();
    }

    @Override
    public void reset() {
        //        addressableMatches.clear();
    }
}

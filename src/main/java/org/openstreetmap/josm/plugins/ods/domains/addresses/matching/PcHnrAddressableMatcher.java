package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.StraightMatch;
import org.openstreetmap.josm.plugins.ods.storage.Index;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

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
        Repository odRepository = module.getOpenDataLayerManager().getRepository();
        Repository osmRepository = module.getOsmLayerManager().getRepository();
        Index<AddressNode> pcHnrIndex = osmRepository.getIndex(AddressNode.PC_FULL_HNR_INDEX_KEY);
        //        odRepository.getAll(Addressable.class)
        //        .filter(a -> a.getMatch(Addressable.class) == null)
        //        .filter(a -> a.getAddress() != null)
        //        .forEach(a -> findMatch(pcHnrIndex, a));
        odRepository.iterator(AddressNode.class).forEachRemaining(addressNode -> {
            if (addressNode.getMatch() == null) {
                findMatch(pcHnrIndex, addressNode);
            }
        });
    }

    private static void findMatch(Index<AddressNode> index, AddressNode odAddressNode) {
        AddressNode[] nodes = index.getAllByTemplate(odAddressNode).toArray(AddressNode[]::new);
        if (nodes.length == 0) return;
        if (nodes.length == 1) {
            createMatch(odAddressNode, nodes[0]);
        }
        else {
            Main.warn("Duplicate match");
        }
    }

    private static void createMatch(AddressNode odAddressNode, AddressNode osmAddressNode) {
        if (!osmAddressNode.getMatch().isPresent()) {
            Match<AddressNode> match = new StraightMatch<>(osmAddressNode);
            odAddressNode.setMatch(match);
            return;
        }
        Main.warn("Complex match");
        //        match.analyze();
        //        match.updateMatchTags();
    }

    @Override
    public void reset() {
        //        addressableMatches.clear();
    }
}

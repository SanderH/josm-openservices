package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Addressable;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.storage.Index;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class PcHnrAddressableMatcher implements Matcher<Addressable> {
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


    @Override
    public Class<Addressable> getType() {
        return Addressable.class;
    }

    @Override
    public void run() {
        Repository odRepository = module.getOpenDataLayerManager().getRepository();
        Repository osmRepository = module.getOsmLayerManager().getRepository();
        Index<Addressable> pcHnrIndex = osmRepository.getIndex(Addressable.PC_FULL_HNR_INDEX_KEY);
        //        odRepository.getAll(Addressable.class)
        //        .filter(a -> a.getMatch(Addressable.class) == null)
        //        .filter(a -> a.getAddress() != null)
        //        .forEach(a -> findMatch(pcHnrIndex, a));
        odRepository.iterator(Addressable.class).forEachRemaining(addressable -> {
            if (addressable.getMatch(Addressable.class) == null) {
                if (addressable.getAddress() != null) {
                    findMatch(pcHnrIndex, addressable);
                }
            }
        });
    }

    private static void findMatch(Index<Addressable> index, Addressable odAddressable) {
        index.getAllByTemplate(odAddressable).forEach(osmAddressable -> {
            Match<Addressable> match = osmAddressable.getMatch(Addressable.class);
            if (match != null) {
                match.addOpenDataEntity(odAddressable);
            }
            else {
                match = new AddressableMatch(osmAddressable, odAddressable);
            }
            match.analyze();
            match.updateMatchTags();
        });
    }

    @Override
    public void reset() {
        //        addressableMatches.clear();
    }
}

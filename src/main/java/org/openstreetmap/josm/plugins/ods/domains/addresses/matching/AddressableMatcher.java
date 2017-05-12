package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Addressable;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class AddressableMatcher implements Matcher<Addressable> {
    private final OdsModule module;
    private final SameBuildingAddressableMatcher byBuildingMatcher;
    private final PcHnrAddressableMatcher pcHnrMatcher;

    private final Map<Object, Match<Addressable>> addressableMatches = new HashMap<>();

    public AddressableMatcher(OdsModule module) {
        super();
        this.module = module;
        this.byBuildingMatcher = new SameBuildingAddressableMatcher(module);
        this.pcHnrMatcher = new PcHnrAddressableMatcher(module);
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
        byBuildingMatcher.run();
        pcHnrMatcher.run();
    }

    public void analyze() {
        for (Match<Addressable> match : addressableMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        Repository repository = module.getOpenDataLayerManager().getRepository();
        repository.getAll(Addressable.class).filter(a -> a.getMatches().isEmpty()).forEach(addressable -> {
            ManagedPrimitive osm = addressable.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, addressable.getStatus().toString());
            }
        });
    }

    @Override
    public void reset() {
        addressableMatches.clear();
    }
}

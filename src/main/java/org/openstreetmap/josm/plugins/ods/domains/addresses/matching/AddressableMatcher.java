package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import java.util.List;
import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.matching.GeometryDifference;
import org.openstreetmap.josm.plugins.ods.matching.StatusDifference;
import org.openstreetmap.josm.plugins.ods.matching.StraightMatch;
import org.openstreetmap.josm.plugins.ods.matching.TagDifference;

public class AddressableMatcher implements Matcher {
    private final SameBuildingAddressableMatcher byBuildingMatcher;
    private final PcHnrAddressableMatcher pcHnrMatcher;

    public AddressableMatcher(OdsModule module) {
        super();
        this.byBuildingMatcher = new SameBuildingAddressableMatcher(module);
        this.pcHnrMatcher = new PcHnrAddressableMatcher(module);
    }

    @Override
    public void initialize() throws OdsException {
        // Empty implementation. No action required
    }

    @Override
    public void run() {
        byBuildingMatcher.run();
        pcHnrMatcher.run();
    }

    //    public void analyze() {
    //        for (Match<Addressable> match : addressableMatches.values()) {
    //            if (match.isSimple()) {
    //                match.analyze();
    //                match.updateMatchTags();
    //            }
    //        }
    //        Repository repository = module.getOpenDataLayerManager().getRepository();
    //        repository.getAll(Addressable.class).filter(a -> a.getMatches().isEmpty()).forEach(addressable -> {
    //            ManagedPrimitive osm = addressable.getPrimitive();
    //            if (osm != null) {
    //                osm.put(ODS.KEY.IDMATCH, "false");
    //                osm.put(ODS.KEY.STATUS, addressable.getStatus().toString());
    //            }
    //        });
    //    }
    //

    public static void analizeGeometry(StraightMatch<AddressNode> match) {
        Building odBuilding = match.getOpenDataEntity().getBuilding();
        Building osmBuilding = match.getOsmEntity().getBuilding();
        boolean different = true;
        if (osmBuilding != null && odBuilding != null) {
            if (Objects.equals(osmBuilding.getReferenceId(), odBuilding.getReferenceId())) {
                different = false;
            }
        }
        if (different) {
            match.addDifference(new GeometryDifference(match));
        }
    }

    private static void analyzeStatus(StraightMatch<AddressNode> match) {
        if (Objects.equals(match.getOpenDataEntity().getStatus(), match.getOsmEntity().getStatus())) {
            return;
        }
        match.addDifference(new StatusDifference(match));
    }

    private static void analyzeAttributes(StraightMatch<AddressNode> match) {
        List<String> differeningKeys = AddressTagMatcher.compare(match.getOpenDataEntity().getAddress(),
                match.getOsmEntity().getAddress());
        for (String key : differeningKeys) {
            match.addDifference(new TagDifference(match, key));
        }
    }

    @Override
    public void reset() {
        // No action required
    }
}

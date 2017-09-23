package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.tools.Logging;

public class AddressableMatcher implements Matcher {
    //    private final SameBuildingAddressableMatcher byBuildingMatcher;
    private final PcHnrAddressableMatcher pcHnrMatcher;

    public AddressableMatcher(OdsModule module) {
        super();
        // TODO Use (DI?) container to retrieve the fields.
        //        this.byBuildingMatcher = new SameBuildingAddressableMatcher(module);
        this.pcHnrMatcher = new PcHnrAddressableMatcher(module);
    }

    @Override
    public void initialize() throws OdsException {
        // Empty implementation. No action required
    }

    @Override
    public void run() {
        //        byBuildingMatcher.run();
        try {
            pcHnrMatcher.run();
        }
        catch (Exception e) {
            Logging.error(e);
            throw e;
        }
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

    //    public static void analizeGeometry(StraightMatch<OpenDataAddressNode, OsmAddressNode> match) {
    //        Building odBuilding = match.getOdEntity().getBuilding();
    //        Building osmBuilding = match.getOsmEntity().getBuilding();
    //        boolean different = true;
    //        if (osmBuilding != null && odBuilding != null) {
    //            if (Objects.equals(osmBuilding.getReferenceId(), odBuilding.getReferenceId())) {
    //                different = false;
    //            }
    //        }
    //        if (different) {
    //            match.setGeometryDifference(new GeometryDifference(match));
    //        }
    //    }
    //
    //    private static void analyzeStatus(StraightMatch<OpenDataAddressNode, OsmAddressNode> match) {
    //        if (Objects.equals(match.getOdEntity().getStatus(), match.getOsmEntity().getStatus())) {
    //            return;
    //        }
    //        match.setStatusDifference(new StatusDifference(match));
    //    }
    //
    //    private static void analyzeAttributes(StraightMatch<OpenDataAddressNode, OsmAddressNode> match) {
    //        OsmAddressNode osmAddressNode = match.getOsmEntity();
    //        OpenDataAddressNode odAddressNode = match.getOdEntity();
    //        List<String> differeningKeys = AddressTagMatcher.compare(odAddressNode.getAddress(),
    //                osmAddressNode.getAddress());
    //        for (String key : differeningKeys) {
    //            match.addAttributeDifference(new TagDifference(match, key));
    //        }
    //    }

    @Override
    public void reset() {
        // No action required
    }
}

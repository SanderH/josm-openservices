package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.osm.alignment.NodeDWithin;
import org.openstreetmap.josm.plugins.ods.osm.alignment.NodeDWithinLatLon;
import org.openstreetmap.josm.plugins.ods.osm.alignment.OsmWayAligner;

public class BuildingAligner extends OsmWayAligner {

    public BuildingAligner(Collection<Way> ways) {
        this(ways, new NodeDWithinLatLon(0.05));
    }

    public BuildingAligner(Collection<Way> ways, NodeDWithin dWithin) {
        super(ways, dWithin, BuildingEntityType::isBuildingWay);
    }
}

package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.command.Command;

public class GeometryDifference implements Difference {
    private final Osm2OdMatch<?> match;

    public GeometryDifference(StraightMatch<?> match2) {
        super();
        this.match = match2;
    }

    @Override
    public List<Command> fix() {
        return Collections.emptyList();
    }

    public Osm2OdMatch<?> getMatch() {
        return match;
    }
}

package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.command.Command;

public class TagDifference implements Difference {
    private final Osm2OdMatch match;
    private final String key;

    public TagDifference(Osm2OdMatch match, String key) {
        super();
        this.match = match;
        this.key = key;
    }

    @Override
    public List<Command> fix() {
        return Collections.emptyList();
    }
}

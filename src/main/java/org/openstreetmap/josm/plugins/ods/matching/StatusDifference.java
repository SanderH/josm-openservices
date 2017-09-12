package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.command.Command;

public class StatusDifference implements Difference {
    private final Osm2OdMatch<?> match;

    public StatusDifference(Osm2OdMatch<?> match) {
        super();
        this.match = match;
    }

    @Override
    public List<Command> fix() {
        return Collections.emptyList();
    }

    public Osm2OdMatch<?> getMatch() {
        return match;
    }
}

package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public class TagDifference implements Difference {
    private final Osm2OdMatch<?> match;
    private final String key;

    public TagDifference(Osm2OdMatch<?> match, String key) {
        super();
        this.match = match;
        this.key = key;
    }

    @Override
    public List<Command> fix() {
        if (match instanceof StraightMatch) {
            StraightMatch<?> straightMatch = (StraightMatch<?>) match;
            Entity<?> odEntity = straightMatch.getOpenDataEntity();
            Entity<?> osmEntity = straightMatch.getOsmEntity();
            Command cmd = osmEntity.getPrimitive().put(key, odEntity.getPrimitive().get(key));
            if (cmd != null) {
                return Collections.singletonList(cmd);
            }
        }
        return Collections.emptyList();
    }
}

package org.openstreetmap.josm.plugins.ods.update;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public interface GeometryUpdater {

    public UpdateResult run(List<Match<? extends Entity>> matches);
}

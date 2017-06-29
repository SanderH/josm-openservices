package org.openstreetmap.josm.plugins.ods.update;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.matching.StraightMatch;

public interface GeometryUpdater {

    public UpdateResult run(List<StraightMatch<?>> matches);
}

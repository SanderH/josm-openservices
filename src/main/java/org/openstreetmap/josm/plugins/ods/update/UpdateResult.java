package org.openstreetmap.josm.plugins.ods.update;

import java.util.Set;

import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface UpdateResult {
    public Set<? extends Entity<?>> getUpdatedEntities();
    public Set<Way> getUpdatedWays();
}

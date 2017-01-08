package org.openstreetmap.josm.plugins.ods.update;

import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public class UpdateResultImpl implements UpdateResult {
    private Set<? extends Entity> updatedEntities = new HashSet<>();
    private Set<Way> updatedWays = new HashSet<>();

    public UpdateResultImpl(Set<? extends Entity> updatedEntities, Set<Way> updatedWays) {
        super();
        this.updatedEntities = updatedEntities;
        this.updatedWays = updatedWays;
    }

    @Override
    public Set<? extends Entity> getUpdatedEntities() {
        return updatedEntities;
    }
    
    @Override
    public Set<Way> getUpdatedWays() {
        return updatedWays;
    }
}
package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Entrance;

public class EntranceImpl extends AbstractEntity implements Entrance {
    private String type = null;
    
    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Class<? extends Entity> getBaseType() {
        return Entrance.class;
    }

}

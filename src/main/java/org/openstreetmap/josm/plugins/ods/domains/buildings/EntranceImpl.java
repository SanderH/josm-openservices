package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

public class EntranceImpl extends AbstractEntity<EntranceEntityType> implements Entrance {
    private String type = null;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}

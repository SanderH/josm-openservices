package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.entities.opendata.AbstractOdEntity;

public class OpenDataEntrance extends AbstractOdEntity<EntranceEntityType> implements Entrance {
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

package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.Osm2OdMatch;

public abstract class AbstractOsmEntity extends AbstractEntity implements OsmEntity {

    private Osm2OdMatch match;
    private boolean geometryUpdateRequired;
    private boolean statusUpdateRequired;
    private boolean taggingUpdateRequired;
    private boolean noEntity;

    public void setMatch(Osm2OdMatch match) {
        this.match = match;
    }

    @Override
    public Osm2OdMatch getMatch() {
        return match;
    }

    @Override
    public boolean isGeometryUpdateRequired() {
        return geometryUpdateRequired;
    }

    @Override
    public boolean isStatusUpdateRequired() {
        return statusUpdateRequired;
    }

    @Override
    public boolean isTaggingUpdateRequired() {
        return taggingUpdateRequired;
    }

    @Override
    public boolean isNoEntity() {
        return noEntity;
    }

    public void setGeometryUpdateRequired(boolean geometryUpdateRequired) {
        this.geometryUpdateRequired = geometryUpdateRequired;
    }

    public void setStatusUpdateRequired(boolean statusUpdateRequired) {
        this.statusUpdateRequired = statusUpdateRequired;
    }

    public void setTaggingUpdateRequired(boolean taggingUpdateRequired) {
        this.taggingUpdateRequired = taggingUpdateRequired;
    }

    public void setNoEntity(boolean noEntity) {
        this.noEntity = noEntity;
    }
}

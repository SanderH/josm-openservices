package org.openstreetmap.josm.plugins.ods.entities.opendata;

import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.matching.Od2OsmMatch;

public abstract class AbstractOdEntity<T extends EntityType> extends AbstractEntity<T>
implements OdEntity<T> {
    private boolean deleted = false;
    private DownloadResponse response;
    private Od2OsmMatch<T> match;

    public void setDownloadResponse(DownloadResponse response) {
        this.response = response;
    }

    @Override
    public DownloadResponse getDownloadResponse() {
        return response;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean isMissing() {
        return getMatch() == null;
    }

    @Override
    public boolean isCreationRequired() {
        return !isMissing();
    }

    @Override
    public boolean isDeletionRequired() {
        return (getMatch() != null) && isDeleted();
    }

    public void setMatch(Od2OsmMatch<T> match) {
        this.match = match;
    }

    @Override
    public Od2OsmMatch<T> getMatch() {
        return match;
    }

    @Override
    public void clearMatchTags() {
        this.getPrimitive().put(ODS.KEY.BASE, null);
    }

    @Override
    public void updateMatchTags() {
        String match = "unknown";
        if (getMatch() != null) {
            match = "up to date";
        }
        if (isDeleted()) {
            match = "deleted";
        }
        if (isDeletionRequired()) {
            match = "deletion required";
        }
        if (isMissing()) {
            match = "missing";
        }
        if (isCreationRequired()) {
            match = "creation required";
        }

        if (isCreationRequired()) {
            match = "creation required";
        }
        getPrimitive().put(ODS.KEY.MATCH, match);
    }
}

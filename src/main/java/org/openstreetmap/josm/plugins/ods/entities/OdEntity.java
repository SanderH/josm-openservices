package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.matching.Od2OsmMatch;

public interface OdEntity<T extends EntityType> extends Entity<T> {

    //    public void setDownloadResponse(DownloadResponse response);
    public Od2OsmMatch getMatch();

    /**
     * Does the open data source report this entity as deleted?
     * Not all open data sources provide this information.
     *
     * @return true if the entity is deleted according to the open
     *  data source. false otherwise
     */
    public boolean isDeleted();

    public void setDeleted(boolean deleted);

    /**
     * Get the download response that imported this entity
     */
    public DownloadResponse getDownloadResponse();

    /**
     * Check if a matching entity exists on the OSM layer
     *
     * @return true if no matching entity exists on the OSM layer
     */
    public boolean isMissing();

    /**
     * Check if this entity should be uploaded to OSM.
     * Please notice that not all missing entities should be uploaded. For
     * example a missing Entity that is 50 year old may have been demolished, but
     * the new state has not been reflected yet in the OpenDate source.
     *
     * @return true if this entity should be uploaded to OSM.
     */
    public boolean isCreationRequired();

    /**
     * Check if this entity should be deleted from OSM.
     * This method should only return true if the deletion was confirmed by the open
     * data source. Otherwise the entity won't be on the OpenData layer anyway.
     *
     * @return true if the entity should be deleted from OSM.
     */
    public boolean isDeletionRequired();

    /**
     * Clear any ODS Match tags on this entity.
     */
    public void clearMatchTags();

    /**
     * Update the ODS match status tags for this entity
     */
    public void updateMatchTags();
}

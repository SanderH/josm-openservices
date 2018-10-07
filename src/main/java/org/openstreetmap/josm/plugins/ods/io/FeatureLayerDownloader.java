package org.openstreetmap.josm.plugins.ods.io;

import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

/**
 * Marker interface
 */
public interface FeatureLayerDownloader {
    //    public abstract void initialize() throws OdsException;

    public void setup(DownloadRequest request) throws OdsException;

    public void setResponse(DownloadResponse response);

    public Task prepare();

    public Task download();

    public Task process();

    public abstract void cancel();
}

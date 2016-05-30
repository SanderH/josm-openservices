package org.openstreetmap.josm.plugins.ods.io;

import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

/**
 * Marker interface
 */
public interface LayerDownloader extends Downloader {
    public abstract void initialize() throws OdsException;

    public void setResponse(DownloadResponse response);
}

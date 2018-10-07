package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.ods.io.MainDownloader;

public abstract class Scenario {
    public void initialize() {
        createMainStorage();
        createDownloadStorage();
        createMappers();
        createHosts();
        createFeatureSources();
        createDataSources();
        createFeatureDownloaders();
        createMainDownloader();
    }

    protected abstract void createHosts();
    protected abstract void createFeatureSources();
    protected abstract void createDataSources();
    protected abstract void createMappers();
    protected abstract void createMainStorage();
    protected abstract void createDownloadStorage();
    protected abstract void createFeatureDownloaders();
    protected abstract void createMainDownloader();

    public abstract MainDownloader getMainDownloader();
}

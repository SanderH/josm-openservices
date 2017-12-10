package org.openstreetmap.josm.plugins.ods.geotools;

import org.openstreetmap.josm.plugins.ods.OdsModule;

public class GtDownloaderFactory {
    private final OdsModule module;

    public GtDownloaderFactory(OdsModule module) {
        super();
        this.module = module;
    }

    public GtDownloader createDownloader(GtDataSource dataSource) {
        return new GtDownloader(module, dataSource);
    }
}

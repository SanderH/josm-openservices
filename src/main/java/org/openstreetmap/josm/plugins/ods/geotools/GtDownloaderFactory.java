package org.openstreetmap.josm.plugins.ods.geotools;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.tools.I18n;

public class GtDownloaderFactory {
    private final OdsModule module;

    public GtDownloaderFactory(OdsModule module) {
        super();
        this.module = module;
    }

    public <T extends EntityType> GtDownloader<T> createDownloader(GtDataSource dataSource, Class<T> type) {
        T entityType = module.getEntityType(type);
        if (entityType == null) {
            throw new RuntimeException(I18n.tr("Unknown entityType: {0}", type.toString()));
        }
        return new GtDownloader<>(module, dataSource, entityType);
    }
}

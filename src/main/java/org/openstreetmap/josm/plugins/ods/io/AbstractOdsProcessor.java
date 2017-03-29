package org.openstreetmap.josm.plugins.ods.io;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;

public abstract class AbstractOdsProcessor implements OdsProcessor {

    /**
     * Convenience method to get access to the active OdsModule object.
     * 
     * @return The active ODS module;
     */
    @SuppressWarnings("static-method")
    protected OdsModule getModule() {
        return OpenDataServicesPlugin.INSTANCE.getActiveModule();
    }
}

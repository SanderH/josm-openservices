package org.openstreetmap.josm.plugins.ods.io;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;

/**
 * Implementations of this class can do some processing on the collected data.
 * The processing is typically, but not necessarily done in the processing phase
 * of a download.
 * For example, an implementation can bind address nodes to a building.
 *
 * @author Gertjan Idema
 */
@Deprecated
public interface OdsProcessor extends Runnable {
    /**
     * Convenience method to get access to the active OdsModule object.
     *
     * @return The active ODS module;
     */
    public static OdsModule getModule() {
        return OpenDataServicesPlugin.INSTANCE.getActiveModule();
    }
}

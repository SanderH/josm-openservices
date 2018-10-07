package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * OdsModulePlugin is the base class for ODS modules that are
 * Josm plug-ins.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class OdsModulePlugin extends Plugin {
    private final OdsModule odsModule;

    public OdsModulePlugin(PluginInformation info, OdsModule odsModule) {
        super(info);
        this.odsModule = odsModule;
        OpenDataServicesPlugin.INSTANCE.registerModule(odsModule);
        odsModule.setPlugin(this);
    }

    public OdsModule getModule() {
        return odsModule;
    }
}

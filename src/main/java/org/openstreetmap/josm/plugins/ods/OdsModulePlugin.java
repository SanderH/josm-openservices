package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * OdsModulePlugin is the base class for ODS modules that are
 * Josm plug-ins.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class OdsModulePlugin extends Plugin {
    private final OdsModule module;

    public OdsModulePlugin(PluginInformation info, Module guiceModule) {
        super(info);
        Injector injector = Guice.createInjector(guiceModule);

        module = injector.getInstance(OdsModule.class);
        OpenDataServicesPlugin.INSTANCE.registerModule(getModule());
        module.setPlugin(this);
        //        module.initialize();
    }

    public OdsModule getModule() {
        return module;
    }
}

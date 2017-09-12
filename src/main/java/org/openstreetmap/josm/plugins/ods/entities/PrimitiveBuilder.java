package org.openstreetmap.josm.plugins.ods.entities;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class PrimitiveBuilder {
    private final List<EntityPrimitiveBuilder<?>> entityBuilders = new LinkedList<>();

    public PrimitiveBuilder(OdsModule module) {
        super();
        for (Class<? extends EntityPrimitiveBuilder<?>> clazz : module.getConfiguration().getPrimitiveBuilders()) {
            try {
                entityBuilders.add(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //    public void register(EntityPrimitiveBuilder<?> builder) {
    //        entityBuilders.add(builder);
    //    }
    //
    public void run(DownloadResponse response) {
        for (EntityPrimitiveBuilder<?> builder : entityBuilders) {
            builder.run();
        }
    }
}

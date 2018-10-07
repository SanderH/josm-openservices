package org.openstreetmap.josm.plugins.ods.entities;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.io.AbstractTask;

public class PrimitiveBuilder extends AbstractTask {
    private final List<EntityPrimitiveBuilder<?>> entityBuilders;

    public PrimitiveBuilder(List<EntityPrimitiveBuilder<?>> entityBuilders) {
        super();
        this.entityBuilders = entityBuilders;
    }

    @Override
    public Void call() throws Exception {
        for (EntityPrimitiveBuilder<?> builder : entityBuilders) {
            builder.run();
        }
        return null;
    }
}

package org.openstreetmap.josm.plugins.ods.entities;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class PrimitiveBuilder {
    private final OdsModule module;
    private final List<EntityPrimitiveBuilder<? extends Entity<?>>> entityBuilders = new LinkedList<>();

    public PrimitiveBuilder(OdsModule module) {
        super();
        this.module = module;
    }

    public <T extends Entity<?>> void register(Class<T> clazz, EntityPrimitiveBuilder<T> builder) {
        entityBuilders.add(builder);
    }

    public void run(DownloadResponse response) {
        for (EntityPrimitiveBuilder<? extends Entity<?>> builder : entityBuilders) {
            buildPrimitives(builder);
        }
    }

    private <E extends Entity<?>> void buildPrimitives(EntityPrimitiveBuilder<E> entityBuilder) {
        Repository repository = module.getRepository();
        repository.getAll(entityBuilder.getEntityClass())
        .forEach(entity -> {
            if (entity.getPrimitive() == null) {
                entityBuilder.createPrimitive(entity);
            }
        });
    }
}

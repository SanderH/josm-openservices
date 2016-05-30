package org.openstreetmap.josm.plugins.ods.entities;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class PrimitiveBuilder {
    private OdsModule module;
    private List<EntityPrimitiveBuilder<? extends Entity>> entityBuilders = new LinkedList<>();
    
    public PrimitiveBuilder(OdsModule module) {
        super();
        this.module = module;
    }

    public <T extends Entity> void register(Class<T> clazz, EntityPrimitiveBuilder<T> builder) {
        entityBuilders.add(builder);
    }
    
    public void run(DownloadResponse response) {
        for (EntityPrimitiveBuilder<? extends Entity> builder : entityBuilders) {
            buildPrimitives(builder);
        }
    }

    private <E extends Entity> void buildPrimitives(EntityPrimitiveBuilder<E> entityBuilder) {
        EntityRepository repository = module.getOpenDataLayerManager().getRepository();
//        EntityStore<E> store = module.getOpenDataLayerManager()
//                .getEntityStore(entityBuilder.getEntityClass());
//        for (E entity : store) {
//            if (entity.getPrimitive() == null) {
//                entityBuilder.createPrimitive(entity);
//            }
//        }
        for (E entity : repository.getAll(entityBuilder.getEntityClass())) {
            if (entity.getPrimitive() == null) {
                entityBuilder.createPrimitive(entity);
            }
        }
    }
}

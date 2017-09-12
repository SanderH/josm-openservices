package org.openstreetmap.josm.plugins.ods.properties;

import java.util.Collections;
import java.util.List;

public class SimpleEntityMapper<T1, T2> implements EntityMapper<T1, T2> {
    private EntityFactory<T2> entityFactory;
    private List<EntityAttributeMapper<T1, T2>> attributeMappers;
    private List<ChildMapper<T1, T2>> childMappers;


    public SimpleEntityMapper(EntityFactory<T2> entityFactory,
            List<EntityAttributeMapper<T1, T2>> attributeMappers) {
        this(entityFactory, attributeMappers, Collections.emptyList());
    }

    public SimpleEntityMapper(EntityFactory<T2> entityFactory,
            List<EntityAttributeMapper<T1, T2>> attributeMappers,
            List<ChildMapper<T1, T2>> childMappers) {
        super();
        this.entityFactory = entityFactory;
        this.attributeMappers = attributeMappers;
        this.childMappers = childMappers;
    }

    //    @Override
    //    public void map(T1 source, T2 target) {
    //        for (EntityAttributeMapper<T1, T2> mapper : attributeMappers)
    //            mapper.map(source, target);
    //        for (ChildMapper<T1, T2> childMapper : childMappers) {
    //            childMapper.map(source, target);
    //        }
    //    }
    //
    //    @Override
    //    public void mapAndConsume(T1 source, Consumer<Object> consumer) {
    //        T2 target = entityFactory.newInstance();
    //        for (EntityAttributeMapper<T1, T2> mapper : attributeMappers)
    //            mapper.map(source, target);
    //        for (ChildMapper<T1, T2> childMapper : childMappers) {
    //            Object child = childMapper.map(source, target);
    //            consumer.accept(child);
    //        }
    //        consumer.accept(target);
    //    }

    @Override
    public T2 map(T1 source) {
        T2 target = entityFactory.newInstance();
        for (EntityAttributeMapper<T1, T2> mapper : attributeMappers)
            mapper.map(source, target);
        for (ChildMapper<T1, T2> childMapper : childMappers) {
            childMapper.map(source, target);
        }
        return target;
    }
}

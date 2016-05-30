package org.openstreetmap.josm.plugins.ods.properties;

public class ChildMapperImpl<T1, T2, T3> implements ChildMapper<T1, T2> {
    private EntityMapper<T1, T3> childEntityMapper;
    private PropertySetter<T2, T3> setter;

    
    public ChildMapperImpl(EntityMapper<T1, T3> childEntityMapper,
            PropertySetter<T2, T3> setter) {
        super();
        this.childEntityMapper = childEntityMapper;
        this.setter = setter;
    }

    @Override
    public T3 map(T1 source, T2 target) {
        T3 value = childEntityMapper.map(source);
        setter.set(target, value);
        return value;
    }
}

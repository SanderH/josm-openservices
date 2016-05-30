package org.openstreetmap.josm.plugins.ods.properties;

import org.openstreetmap.josm.plugins.ods.properties.transform.TypeTransform;

public class TransformingEntityAttributeMapper<T1, T2, A1, A2>
        implements EntityAttributeMapper<T1, T2> {

    private PropertyGetter<T1, A1> getter;
    private PropertySetter<T2, A2> setter;
    private TypeTransform<A1, A2> transform;
    
    public TransformingEntityAttributeMapper(PropertyGetter<T1, A1> getter,
            PropertySetter<T2, A2> setter, TypeTransform<A1, A2> transform) {
        super();
        this.getter = getter;
        this.setter = setter;
        this.transform = transform;
    }

    @Override
    public void map(T1 source, T2 target) {
        A1 value = getter.get(source);
        A2 transformed = transform.apply(value);
        setter.set(target, transformed);
    }
}

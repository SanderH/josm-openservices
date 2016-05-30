package org.openstreetmap.josm.plugins.ods.properties;

public class SimpleEntityAttributeMapper<T1, T2, T3>
        implements EntityAttributeMapper<T1, T2> {

    private PropertyGetter<T1, T3> getter;
    private PropertySetter<T2, T3> setter;
    
    public SimpleEntityAttributeMapper(PropertyGetter<T1, T3> getter,
            PropertySetter<T2, T3> setter) {
        super();
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void map(T1 source, T2 target) {
        T3 value = getter.get(source);
        setter.set(target, value);
    }
}

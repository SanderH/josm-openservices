package org.openstreetmap.josm.plugins.ods.properties;

public class AttributeMapperImpl<T1, T2, T3> implements AttributeMapper<T1, T2> {
    private PropertyGetter<T1, T3> propertyGetter;
    private PropertySetter<T2, T3> propertySetter;
    
    public AttributeMapperImpl(PropertyGetter<T1, T3> propertyGetter,
            PropertySetter<T2, T3> propertySetter) {
        super();
        this.propertyGetter = propertyGetter;
        this.propertySetter = propertySetter;
    }

    @Override
    public void map(T1 source, T2 target) {
        T3 value = propertyGetter.get(source);
        propertySetter.set(target, value);
    }
}

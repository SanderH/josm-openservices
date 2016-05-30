package org.openstreetmap.josm.plugins.ods.properties;

public class ConstantAttributeMapper<T1, T2, A1>
        implements EntityAttributeMapper<T1, T2> {
    private PropertyHandler<T2, A1> targetAttributeHandler;
    private A1 value;

    public ConstantAttributeMapper(PropertyHandler<T2, A1> targetAttributeHandler, A1 value) {
        super();
        this.targetAttributeHandler = targetAttributeHandler;
        this.value = value;
    }

    @Override
    public void map(T1 source, T2 target) {
        targetAttributeHandler.set(target, value);
    }
}

package org.openstreetmap.josm.plugins.ods.properties;

public class SimpleEntityFactory<T1, T2 extends T1> implements EntityFactory<T1> {
    private Class<T2> implementingClass;

    public SimpleEntityFactory(Class<T2> implementingClass) {
        super();
        this.implementingClass = implementingClass;
    }

    @Override
    public T1 newInstance() {
        try {
            return implementingClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

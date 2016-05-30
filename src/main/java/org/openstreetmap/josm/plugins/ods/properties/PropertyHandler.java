package org.openstreetmap.josm.plugins.ods.properties;

public interface PropertyHandler<T1, T2> extends PropertySetter<T1, T2>, PropertyGetter<T1, T2>{

    Class<T2> getType();

}

package org.openstreetmap.josm.plugins.ods.geotools;

public class Attribute<T> {
    public String name;
    public Class<T> clazz;

    public Attribute(String name, Class<T> clazz) {
        super();
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
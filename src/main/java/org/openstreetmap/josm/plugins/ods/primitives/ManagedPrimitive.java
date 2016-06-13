package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Map;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Envelope;

public interface ManagedPrimitive<T extends OsmPrimitive> {
    public Long getUniqueId();
    public T getPrimitive();
    public <E extends Entity> void setEntity(E entity);
    public Envelope getEnvelope();
    public BBox getBBox();
    public boolean isIncomplete();

    public Entity getEntity();
//    public Map<String, String> getKeys();
//    public void put(String key, String value);
//    public String get(String string);

    /**
     * Create the OsmPrimitive(s) in the OSM DataSet. And add the reference to the newly
     * created primitive.
     * If the Osm primitive already exists. Just return it.
     *
     * 
     * @param dataSet
     * @return 
     */
    public T create(DataSet dataSet);
    
    public void put(String key, String value);
    public String get(String key);
    public void putAll(Map<String, String> tags);
    public Map<String, String> getKeys();
    public void remove(String string);
}

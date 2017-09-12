package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;
import java.util.Map;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface ManagedPrimitive {
    public LayerManager getLayerManager();
    public Long getUniqueId();
    public void setPrimitive(OsmPrimitive primitive);
    public OsmPrimitive getPrimitive();
    public Collection<ManagedPrimitive> getReferrers();
    public <E extends Entity<?>> void setEntity(E entity);
    public boolean isIncomplete();
    public boolean contains(ManagedNode mNode);

    public Entity<?> getEntity();

    /**
     * Create the OsmPrimitive(s) in the OSM DataSet. And add the reference to the newly
     * created primitive.
     * If the Osm primitive already exists. Just return it.
     *
     *
     * @param dataSet
     * @return
     */
    public OsmPrimitive create(DataSet dataSet);

    public Command put(String key, String value);
    public String get(String key);
    public Command putAll(Map<String, String> tags);
    public Map<String, String> getKeys();
    public void remove(String string);
    public BBox getBBox();
    public double getArea();
    public LatLon getCenter();
    public void geometryChanged();
}

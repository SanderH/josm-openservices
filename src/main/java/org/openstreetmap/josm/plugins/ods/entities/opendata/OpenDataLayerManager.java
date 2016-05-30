package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.util.Collections;

import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;
import org.openstreetmap.josm.plugins.ods.osm.ManagedNodeSet;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * The OpenDataLayerManager manages the layer containing the data that has been
 * imported from an open data source.
 * As opposed to the OsmDataLayerManager that manages data from
 * the OSM server.
 * 
 * @author Gertjan Idema
 * 
 */
public class OpenDataLayerManager extends AbstractLayerManager {
    private ManagedNodeSet managedNodes = new ManagedNodeSet();
    private Geometry boundary;

    public OpenDataLayerManager(String name) {
        super(name);
    }

    @Override
    public boolean isOsm() {
        return false;
    }

    @Override
    public ManagedNodeSet getManagedNodes() {
        return managedNodes;
    }

    public Geometry getBoundary() {
        if (boundary == null) {
            boundary = new GeometryFactory().buildGeometry(Collections.emptyList());
        }
        return boundary;
    }

    public void extendBoundary(Geometry boundary) {
        if (this.boundary == null) {
            this.boundary = boundary;
        } else {
            this.boundary = this.boundary.union(boundary);
        }
    }
}

package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

/**
 * Enricher to update the completeness parameter for an open data building;
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingCompletenessEnricher implements Consumer<Building> {
    PreparedPolygon boundary;
    
    public BuildingCompletenessEnricher(Geometry layerBoundary) {
        super();
        try {
            this.boundary = new PreparedPolygon((Polygonal) layerBoundary);
        } catch (ClassCastException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void accept(Building building) {
        if (building.isIncomplete() && boundary.covers(building.getGeometry())) {
            building.setIncomplete(false);
        }
    }
}

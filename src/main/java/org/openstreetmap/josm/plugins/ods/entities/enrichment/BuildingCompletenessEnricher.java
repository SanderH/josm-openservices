package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingEntityType;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

/**
 * Enricher to update the completeness parameter for an open data building;
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingCompletenessEnricher implements Consumer<Entity<BuildingEntityType>> {
    List<PreparedPolygon> boundaries = new LinkedList<>();

    public BuildingCompletenessEnricher(Geometry layerBoundary) {
        super();
        for (int i = 0; i < layerBoundary.getNumGeometries(); i++) {
            Polygonal polygonal = (Polygonal) layerBoundary.getGeometryN(i);
            boundaries.add(new PreparedPolygon(polygonal));
        }
    }

    @Override
    public void accept(Entity<BuildingEntityType> building) {
        if (!building.isIncomplete()) {
            return;
        }
        for (PreparedPolygon prep : boundaries) {
            if (prep.covers(building.getGeometry())) {
                building.setIncomplete(false);
                break;
            }
        }
    }
}

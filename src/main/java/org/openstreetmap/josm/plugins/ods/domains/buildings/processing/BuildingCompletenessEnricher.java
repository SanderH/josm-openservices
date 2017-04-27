package org.openstreetmap.josm.plugins.ods.domains.buildings.processing;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.io.OdsProcessor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

/**
 * Enricher to update the completeness parameter for an open data building;
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingCompletenessEnricher implements OdsProcessor {
    private final OdsModule module = OdsProcessor.getModule();
    List<PreparedPolygon> boundaries = new LinkedList<>();

    public BuildingCompletenessEnricher() {
        super();
    }

    @Override
    public void run() {
        OpenDataLayerManager layerManager = module.getOpenDataLayerManager();
        Geometry boundary = layerManager.getBoundary();
        for (int i = 0; i < boundary.getNumGeometries(); i++) {
            Polygonal polygonal = (Polygonal) boundary.getGeometryN(i);
            boundaries.add(new PreparedPolygon(polygonal));
        }
        layerManager.getRepository().getAll(Building.class)
        .forEach(this::enrich);
    }

    public void enrich(Building building) {
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

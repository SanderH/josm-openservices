package org.openstreetmap.josm.plugins.ods.domains.buildings.processing;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.io.AbstractTask;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

/**
 * Enricher to update the completeness parameter for an open data building;
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingCompletenessEnricher extends AbstractTask {
    private final OdsModule module = OpenDataServicesPlugin.getModule();
    List<PreparedPolygon> boundaries = new LinkedList<>();

    public BuildingCompletenessEnricher() {
        super();
    }

    @Override
    public Void call() {
        OpenDataLayerManager layerManager = module.getOpenDataLayerManager();
        Geometry boundary = layerManager.getBoundary();
        for (int i = 0; i < boundary.getNumGeometries(); i++) {
            Polygonal polygonal = (Polygonal) boundary.getGeometryN(i);
            boundaries.add(new PreparedPolygon(polygonal));
        }
        module.getRepository().query(OpenDataBuilding.class)
        .forEach(this::enrich);
        return null;
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

package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.GeoEntityRepository;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedJosmMultiPolygon;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRing;

public class BuildingAligner {
    private NodeDWithin dWithin;
    private boolean undoable;
    private GeoEntityRepository repository;
    
    public BuildingAligner(OdsModule module, LayerManager layerManager) {
        this.repository = layerManager.getRepository();
//        this.tolerance = module.getTolerance();
//        this.tolerance = 0.1; // Tolerance in meters
        this.dWithin = new NodeDWithinLatLon(0.1);
    }

    public void align(Building building) {
        GeoIndex<Building> index = repository.getGeoIndex(Building.class, "geometry");
        for (Building candidate : index.intersection(building.getGeometry())) {
            if (candidate == building) {
                continue;
            }
            if (building.getNeighbours().contains(candidate)) continue;
            building.getNeighbours().add(candidate);
            candidate.getNeighbours().add(building);
            align(building, candidate);
        }
    }
    
    public void align(Building b1, Building b2) {
        align(b1.getPrimitive(), b2.getPrimitive());
    }
    
    public void align(ManagedPrimitive<?> osm1, ManagedPrimitive<?> osm2) {
        if (osm1 == null || osm2 == null) return;
        ManagedRing<?> ring1 = getOuterWay(osm1);
        ManagedRing<?> ring2 = getOuterWay(osm2);
        if (ring1 != null && ring2 != null) {
            WayAligner wayAligner = new WayAligner(ring1, ring2, dWithin, undoable);
            wayAligner.run();
        }
    }
    
    private ManagedRing<?> getOuterWay(ManagedPrimitive<?> primitive) {
        if (primitive instanceof ManagedRing) {
            return (ManagedRing<?>) primitive;
        }
        if (primitive instanceof ManagedJosmMultiPolygon) {
            ManagedJosmMultiPolygon mpg = (ManagedJosmMultiPolygon) primitive;
            if (mpg.outerRings().size() == 1) {
                return mpg.outerRings().iterator().next();
            }
            Main.warn("Aligning of multipolygon is not supported yet");
            return null;
        }
        return null;
    }
}

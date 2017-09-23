package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.matching.GeometryDifference;
import org.openstreetmap.josm.plugins.ods.matching.StatusDifference;
import org.openstreetmap.josm.plugins.ods.matching.TagDifference;

public class ComplexBagBuildingMatch implements BuildingMatch {
    private final Collection<? extends OpenDataBuilding> odBuildings;
    private final Collection<? extends OsmBuilding> osmBuildings;

    ComplexBagBuildingMatch(Collection<? extends OpenDataBuilding> odBuildings,
            Collection<? extends OsmBuilding> osmBuildings) {
        this.odBuildings = odBuildings;
        this.osmBuildings = osmBuildings;
    }

    @Override
    public void clearDifferences() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasDifferences() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StatusDifference getStatusDifference() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setStatusDifference(StatusDifference statusDifference) {
        // TODO Auto-generated method stub

    }

    @Override
    public GeometryDifference getGeometryDifference() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGeometryDifference(GeometryDifference geometryDifference) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<TagDifference> getAttributeDifferences() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addAttributeDifference(TagDifference difference) {
        // TODO Auto-generated method stub

    }
}

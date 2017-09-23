package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.matching.GeometryDifference;
import org.openstreetmap.josm.plugins.ods.matching.StatusDifference;
import org.openstreetmap.josm.plugins.ods.matching.TagDifference;

public class SimpleBagBuildingMatch implements BuildingMatch {
    private final OpenDataBuilding odBuilding;
    private final OsmBuilding osmBuilding;

    SimpleBagBuildingMatch(OpenDataBuilding odBuilding, OsmBuilding osmBuilding) {
        this.odBuilding = odBuilding;
        this.osmBuilding = osmBuilding;
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

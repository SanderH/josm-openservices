package org.openstreetmap.josm.plugins.ods.entities.actual;

import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public class BuildingType {
    private OdsTagMap tags;
    
    
    public BuildingType(String[][] theTags) {
        super();
        this.tags = new OdsTagMap(theTags);
    }

    public static BuildingType UNCLASSIFIED = new BuildingType(
        new String[][] {{"building", "yes"}});
    
    public static BuildingType HOUSE = new BuildingType(
        new String[][] {{"building", "house"}});
    
    public static BuildingType HOUSEBOAT = new BuildingType(
        new String[][] {{"building", "houseboat"},
            {"floating", "yes"}});
    
    public static BuildingType STATIC_CARAVAN = new BuildingType(
        new String[][] {{"building", "house"}});

    public static BuildingType INDUSTRIAL = new BuildingType(
        new String[][] {{"building", "industrial"}});

    public static BuildingType RETAIL = new BuildingType(
            new String[][] {{"building", "retail"}});

    public static BuildingType OFFICE = new BuildingType(
            new String[][] {{"building", "office"}});
    
    public static BuildingType APARTMENTS = new BuildingType(
            new String[][] {{"building", "apartments"}});
    
    public static BuildingType GARAGE = new BuildingType(
            new String[][] {{"building", "garage"}});
    
    public static BuildingType SUBSTATION = new BuildingType(
            new String[][] {{"building", "yes"},
                {"power", "substation"}});
    
    public static BuildingType PRISON = new BuildingType(
            new String[][] {{"building", "yes"},
                {"amenity", "prison"}});

    public OdsTagMap getTags() {
        return tags;
    }
    
    public static BuildingType OTHER(String subType) {
        return new BuildingType.OTHER(subType);
    }
    
    public static class OTHER extends BuildingType {
        private String subType;

        public OTHER(String subType) {
            super(new String[][] {{"building", subType}});
        }

        public String getSubType() {
            return subType;
        }

        @Override
        public int hashCode() {
            return subType.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) return false;
            if (!(other instanceof OTHER)) {
                return false;
            }
            OTHER o = (OTHER) other;
            return Objects.equals(subType, o.getSubType());
        }
    }
}

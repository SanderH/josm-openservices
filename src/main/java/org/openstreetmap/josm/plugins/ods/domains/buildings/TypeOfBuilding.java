package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public class TypeOfBuilding {
    private final OdsTagMap tags;

    public TypeOfBuilding(String[][] theTags) {
        super();
        this.tags = new OdsTagMap(theTags);
    }

    public static TypeOfBuilding UNCLASSIFIED = new TypeOfBuilding(
            new String[][] {{"building", "yes"}});

    public static TypeOfBuilding HOUSE = new TypeOfBuilding(
            new String[][] {{"building", "house"}});

    public static TypeOfBuilding HOUSEBOAT = new TypeOfBuilding(
            new String[][] {{"building", "houseboat"},
                {"floating", "yes"}});

    public static TypeOfBuilding STATIC_CARAVAN = new TypeOfBuilding(
            new String[][] {{"building", "house"}});

    public static TypeOfBuilding INDUSTRIAL = new TypeOfBuilding(
            new String[][] {{"building", "industrial"}});

    public static TypeOfBuilding RETAIL = new TypeOfBuilding(
            new String[][] {{"building", "retail"}});

    public static TypeOfBuilding OFFICE = new TypeOfBuilding(
            new String[][] {{"building", "office"}});

    public static TypeOfBuilding APARTMENTS = new TypeOfBuilding(
            new String[][] {{"building", "apartments"}});

    public static TypeOfBuilding GARAGE = new TypeOfBuilding(
            new String[][] {{"building", "garage"}});

    public static TypeOfBuilding SUBSTATION = new TypeOfBuilding(
            new String[][] {{"building", "yes"},
                {"power", "substation"}});

    public static TypeOfBuilding PRISON = new TypeOfBuilding(
            new String[][] {{"building", "yes"},
                {"amenity", "prison"}});

    public OdsTagMap getTags() {
        return tags;
    }

    public static TypeOfBuilding OTHER(String subType) {
        return new TypeOfBuilding.OTHER(subType);
    }

    public static class OTHER extends TypeOfBuilding {
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

package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.storage.IdentitySet;

public class OsmBuildingStorage {
    final IdentitySet<OsmBuilding> all = new IdentitySet<>();
    final Map<Object, IdentitySet<OsmBuilding>> idIndex = new HashMap<>();

    public void add(OsmBuilding building) {
        all.add(building);
        IdentitySet<OsmBuilding> set = idIndex.get(building.getReferenceId());
        if (set == null) {
            set = new IdentitySet<>();
            idIndex.put(building.getReferenceId(), set);
        }
        set.add(building);
    }

    public static class Dao implements OsmBuildingDao {
        private final OsmBuildingStorage storage;

        public Dao(OsmBuildingStorage storage) {
            super();
            this.storage = storage;
        }

        @Override
        public void add(OsmBuilding building) {
            storage.all.add(building);
        }

        @Override
        public IdentitySet<? extends OsmBuilding> listById(Object id) {
            IdentitySet<? extends OsmBuilding> set = storage.idIndex.get(id);
            if (set == null) return new IdentitySet<>();
            return set;
        }
    }
}

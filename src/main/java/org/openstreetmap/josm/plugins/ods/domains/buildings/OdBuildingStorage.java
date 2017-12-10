package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.stream.Stream;

import javax.inject.Inject;

import org.openstreetmap.josm.plugins.ods.storage.IdentitySet;

public class OdBuildingStorage {
    final IdentitySet<OpenDataBuilding> all = new IdentitySet<>();

    public void add(OpenDataBuilding building) {
        all.add(building);
    }

    public static class Dao implements OdBuildingDao {
        private final OdBuildingStorage storage;

        @Inject
        public Dao(OdBuildingStorage storage) {
            super();
            this.storage = storage;
        }

        @Override
        public Stream<OpenDataBuilding> getAll() {
            return storage.all.stream();
        }

        @Override
        public void add(OpenDataBuilding building) {
            storage.all.add(building);
        }
    }
}

package org.openstreetmap.josm.plugins.ods.domains.addresses;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.storage.IndexKey;
import org.openstreetmap.josm.plugins.ods.storage.IndexKeyImpl;

public interface Addressable extends Entity {
    public static Function<Addressable, Object> PC_HNR_INDEX_FUNCTION = addressable->{
        Address address = addressable.getAddress();
        return Address.PC_HNR_INDEX_FUNCTION.apply(address);
    };
    public static Function<Addressable, Object> PC_FULL_HNR_INDEX_FUNCTION = addressable->{
        Address address = addressable.getAddress();
        return Address.PC_FULL_HNR_INDEX_FUNCTION.apply(address);
    };
    public static IndexKey<Addressable> PC_HNR_INDEX_KEY =
            new IndexKeyImpl<>(Addressable.class, PC_HNR_INDEX_FUNCTION);
    public static IndexKey<Addressable> PC_FULL_HNR_INDEX_KEY =
            new IndexKeyImpl<>(Addressable.class, PC_FULL_HNR_INDEX_FUNCTION);

    public void setAddress(Address address);
    /**
     * Return the address information.
     *
     * @return null if no address is associated with this object
     */
    public Address getAddress();

    public Building getBuilding();

    public static Set<Object> getBuildingIds(Collection<? extends Addressable> addressables) {
        if (addressables.size() == 1) {
            return addressables.iterator().next().getBuildingIds();
        }
        Set<Object> ids = new HashSet<>();
        for (Addressable addressable : addressables) {
            ids.addAll(addressable.getBuildingIds());
        }
        return ids;
    }

    public Set<Object> getBuildingIds();
}

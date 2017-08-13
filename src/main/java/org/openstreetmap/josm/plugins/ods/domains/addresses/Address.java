package org.openstreetmap.josm.plugins.ods.domains.addresses;

import java.util.Arrays;
import java.util.function.Function;

import org.openstreetmap.josm.plugins.ods.domains.places.City;
import org.openstreetmap.josm.plugins.ods.domains.streets.Street;
import org.openstreetmap.josm.plugins.ods.storage.IndexKey;
import org.openstreetmap.josm.plugins.ods.storage.IndexKeyImpl;

/**
 * Base interface for addresses.
 * An address is not a stand-alone entity. It is always part of an entity. Most of the
 * (Usually an addressNode or a building).
 * TODO houseLetter and HousenumberExtra are specific for the Netherlands. Move
 *  these out of the Address interface into a specific Dutch implementation.
 *
 * @author Gertjan Idema
 *
 */
public interface Address {
    static Function<Address, Object> PC_HNR_INDEX_FUNCTION = (address->{
        if (address == null) return null;
        return Arrays.asList(Address.normalizePostcode(address.getPostcode()),
                address.getHouseNumber());
    });
    static Function<Address, Object> PC_FULL_HNR_INDEX_FUNCTION = (address->{
        if (address == null) return null;
        return  Arrays.asList(Address.normalizePostcode(address.getPostcode()),
                address.getHouseNumber(), address.getHouseLetter(), address.getHouseNumberExtra());
    });
    public static IndexKey<Address> PC_HNR_INDEX_KEY =
            new IndexKeyImpl<>(Address.class, PC_HNR_INDEX_FUNCTION);
    public static IndexKey<Address> PC_FULL_HNR_INDEX_KEY =
            new IndexKeyImpl<>(Address.class, PC_FULL_HNR_INDEX_FUNCTION);

    public void setStreet(Street street);

    public void setHouseNumber(Integer houseNumber);

    public void setHouseName(String houseName);

    public void setHouseLetter(Character houseLetter);

    public void setHouseNumberExtra(String housenumberExtra);

    public void setPostcode(String postcode);

    public void setFullHouseNumber(String fullHouseNumber);

    public void setCityName(String cityName);

    public void setStreetName(String streetName);

    public Integer getHouseNumber();

    public Character getHouseLetter();

    public String getHouseNumberExtra();

    public String getFullHouseNumber();

    public String getHouseName();

    public String getStreetName();

    public Street getStreet();

    public String getPostcode();

    public String getCityName();

    public City getCity();

    public static String normalizePostcode(String postcode) {
        if (postcode == null) {
            return postcode;
        }
        return postcode.replace(" ", "");
    }
}

package org.openstreetmap.josm.plugins.ods.entities.actual;

public interface MutableAddress extends Address {

    @Override
    void setHouseName(String houseName);

    @Override
    void setCityName(String cityName);

    @Override
    void setStreetName(String streetName);

    @Override
    void setPostcode(String postcode);

    @Override
    void setHouseNumber(Integer houseNumber);

    @Override
    void setHouseLetter(Character houseLetter);
    
    @Override
    void setHouseNumberExtra(String extra);

    @Override
    void setFullHouseNumber(String fullHouseNumber);

    @Override
    void setStreet(Street street);
}

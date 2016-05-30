package org.openstreetmap.josm.plugins.ods.entities.actual;

public interface Address {
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
}

package org.openstreetmap.josm.plugins.ods.domains.addresses;

import org.openstreetmap.josm.plugins.ods.domains.places.City;
import org.openstreetmap.josm.plugins.ods.domains.streets.Street;

public class AddressImpl implements Address {
    private Integer houseNumber;
    private Character houseLetter;
    private String houseNumberExtra;
    private String fullHouseNumber;
    private String postcode;
    private String houseName;
    private String streetName;
    private Street street;
    private String cityName;
    private City city;

    @Override
    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    @Override
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
    
    @Override
    public String getStreetName() {
        return streetName;
    }

    @Override
    public Street getStreet() {
        return street;
    }

    @Override
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }
    
    @Override
    public void setFullHouseNumber(String fullHouseNumber) {
        this.fullHouseNumber = fullHouseNumber;
        this.parseHouseNumberParts();
    }
    
    @Override
    public Integer getHouseNumber() {
        return houseNumber;
    }

    @Override
    public String getFullHouseNumber() {
        if (fullHouseNumber == null) {
            fullHouseNumber = formatHouseNumber();
        }
        return fullHouseNumber;
    }

    
    @Override
    public Character getHouseLetter() {
        return houseLetter;
    }

    @Override
    public void setHouseLetter(Character houseLetter) {
        this.houseLetter = houseLetter;
    }

    @Override
    public String getHouseNumberExtra() {
        return houseNumberExtra;
    }

    @Override
    public void setHouseNumberExtra(String houseNumberExtra) {
        this.houseNumberExtra = houseNumberExtra;
    }

    @Override
    public String getHouseName() {
        return houseName;
    }

    @Override
    public String getCityName() {
        return cityName;
    }

    @Override
    public City getCity() {
        return city;
    }

    @Override
    public void setStreet(Street street) {
        this.street = street;
        this.setStreetName(street.getName());
    }
    
    public String formatHouseNumber() {
        StringBuilder sb = new StringBuilder(10);
        if (getHouseNumber() != null) {
            sb.append(getHouseNumber());
        }
        if (getHouseLetter() != null) {
            sb.append(getHouseLetter());
        }
        if (getHouseNumberExtra() != null) {
            sb.append("-").append(getHouseNumberExtra());
        }
        return sb.toString();
    }

    protected void parseHouseNumberParts() {
        String s = this.fullHouseNumber;
        int i=0;
        while (i<s.length() && Character.isDigit(s.charAt(i))) {
            i++;
        }
        if (i > 0) {
            setHouseNumber(Integer.valueOf(s.substring(0, i)));
        }
        if (i >= s.length()) return;
        if (Character.isAlphabetic(s.charAt(i))) {
            setHouseLetter(s.charAt(i));
            i++;
        }
        if (i >= s.length()) return;
        if (s.charAt(i) == '-' || s.charAt(i) == ' ') {
            i++;
        }
        if (i < s.length()) {
            setHouseNumberExtra(s.substring(i));
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStreetName()).append(" ");
        sb.append(getHouseNumber());
        if (getHouseLetter() != null) {
            sb.append(getHouseLetter());
        }
        if (getHouseNumberExtra() != null) {
            sb.append('-').append(getHouseNumberExtra());
        }
        if (getPostcode() != null) {
            sb.append(' ').append(getPostcode());
        }
        if (getCityName() != null) {
            sb.append(' ').append(getCityName());
        }
        return sb.toString();
    }
}

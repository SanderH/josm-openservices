package org.openstreetmap.josm.plugins.ods.domains.addresses.matching;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Addressable;

public class AddressTagMatcher {

    public AddressTagMatcher() {
    }

    public static List<String> compare(Addressable odAddressable, Addressable osmAddressable) {
        return AddressTagMatcher.compare(odAddressable.getAddress(), osmAddressable.getAddress());
    }

    public static List<String> compare(Address odAddress, Address osmAddress) {
        List<String> differingKeys = new LinkedList<>();
        if (!Objects.equals(odAddress.getFullHouseNumber(), osmAddress.getFullHouseNumber())) {
            differingKeys.add("addr:housenumber");
        }
        if (!Objects.equals(Address.normalizePostcode(odAddress.getPostcode()),
                Address.normalizePostcode(osmAddress.getPostcode()))) {
            differingKeys.add("addr:postcode");
        }
        if (!Objects.equals(odAddress.getStreetName(), osmAddress.getStreetName())) {
            differingKeys.add("addr:street");
        }
        if (!Objects.equals(odAddress.getCityName(), osmAddress.getCityName())) {
            differingKeys.add("addr:street");
        }
        return differingKeys;
    }
}
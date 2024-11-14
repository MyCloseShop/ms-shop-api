package com.etna.gpe.mycloseshop.ms_shop_api.dtos.location;

import com.fasterxml.jackson.annotation.JsonProperty;


public class CreateLocationDto {
    private String address;
    private String city;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("optional_information")
    private String optionalInformation;

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getOptionalInformation() {
        return optionalInformation;
    }
}

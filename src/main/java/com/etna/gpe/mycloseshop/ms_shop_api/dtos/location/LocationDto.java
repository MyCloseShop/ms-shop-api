package com.etna.gpe.mycloseshop.ms_shop_api.dtos.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record LocationDto(
        UUID id,
        String address,
        String city,
        String postalCode,
        @JsonProperty("optional_information")
        String optionalInformation,
        @JsonProperty("shop_id")
        UUID shopId
) {
}

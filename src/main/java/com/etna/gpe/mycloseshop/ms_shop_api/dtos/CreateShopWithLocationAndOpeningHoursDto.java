package com.etna.gpe.mycloseshop.ms_shop_api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class CreateShopWithLocationAndOpeningHoursDto {
    private String name;
    private String description;
    private UUID userId;
    /** Note:
     * Remind to ask if in this case OpeningHoursDto should be
     * used as a list of id or a list of OpeningHoursDto
     * Because in OpeningHoursDto there a field shop
    **/
    @JsonProperty("opening_hours")
    private List<CreateOpeningHoursDto> openingHours;
    private CreateLocationDto location;

    public CreateLocationDto getLocation() {
        return location;
    }

    public List<CreateOpeningHoursDto> getOpeningHours() {
        return openingHours;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}

package com.etna.gpe.mycloseshop.ms_shop_api.dtos.opening_hours;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record OpeningHoursDto(
        UUID id,
        String dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        @JsonProperty("shop_id")
        UUID shopId
) {
}

package com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record CreateAppointmentRequest(
        @JsonProperty("shop_id")
        UUID shopId,
        LocalDate date,
        @JsonProperty("start_time")
        LocalTime startTime,
        @JsonProperty("service_id")
        UUID serviceId,
        @JsonProperty("client_id")
        UUID clientId
) {
}

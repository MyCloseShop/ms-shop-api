package com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment;

import com.etna.gpe.mycloseshop.ms_shop_api.entity.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record AppointmentResponse(
        @JsonProperty("appointment_id")
        UUID appointmentId,
        UUID shopId,
        @JsonProperty("service_id")
        UUID serviceId,
        @JsonProperty("client_id")
        UUID clientId,
        LocalDate date,
        @JsonProperty("start_time")
        LocalTime startTime,
        @JsonProperty("end_time")
        LocalTime endTime,
        AppointmentStatus status
) {
}

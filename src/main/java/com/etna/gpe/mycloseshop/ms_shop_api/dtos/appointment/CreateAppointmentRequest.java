package com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment;

import com.etna.gpe.mycloseshop.ms_shop_api.enums.AppointmentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record CreateAppointmentRequest(
        @JsonProperty("shop_id")
        @NotNull(message = "shopId is required")
        UUID shopId,

        @NotNull(message = "date is required")
        LocalDate date,

        @JsonProperty("start_time")
        @NotNull(message = "startTime is required")
        LocalTime startTime,

        @JsonProperty("appointment_type")
        @NotNull(message = "appointmentType is required")
        AppointmentType appointmentType,

        @Min(value = 1, message = "Duration must be at least 1 minute")
        @Max(value = 720, message = "Duration cannot exceed 720 minutes (12 hours)")
        Integer duration,

        @JsonProperty("service_id")
        UUID serviceId,

        @JsonProperty("client_id")
        UUID clientId
) {
}

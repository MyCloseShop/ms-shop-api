package com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.ShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.AppointmentStatus;
import com.etna.gpe.mycloseshop.ms_shop_api.enums.AppointmentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record AppointmentDto(
        UUID id,
        ShopDto shop,
        @JsonProperty("appointment_type")
        AppointmentType appointmentType,
        @JsonProperty("service_id")
        UUID serviceId,
        UUID clientId,
        LocalDate date,
        @JsonProperty("start_time")
        LocalTime startTime,
        @JsonProperty("end_time")
        LocalTime endTime,
        AppointmentStatus status
) {
}

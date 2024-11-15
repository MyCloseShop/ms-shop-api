package com.etna.gpe.mycloseshop.ms_shop_api.dtos.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record ServiceDto(
        int id,
        String name,
        String description,
        double price,
        int duration,
        @JsonProperty("shop_id")
        UUID shopId,
        String createdAt,
        String updatedAt
) {
}

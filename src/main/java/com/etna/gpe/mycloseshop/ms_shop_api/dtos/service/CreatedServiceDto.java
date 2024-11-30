package com.etna.gpe.mycloseshop.ms_shop_api.dtos.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record CreatedServiceDto(
        UUID id,
        String name,
        String description,
        double price,
        int duration,
        @JsonProperty(value = "shop_id")
        UUID shopId,
        String createdAt,
        String updatedAt
) {
}

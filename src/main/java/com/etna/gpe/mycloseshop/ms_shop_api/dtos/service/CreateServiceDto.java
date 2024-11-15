package com.etna.gpe.mycloseshop.ms_shop_api.dtos.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Builder(toBuilder = true)
public record CreateServiceDto(
        @Length(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        @NotNull(message = "Name is required")
        String name,
        @Length(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
        String description,
        Double price,
        @NotNull(message = "Duration is required")
        Integer duration,
        @JsonProperty(value = "shop_id", required = true)
        @NotNull(message = "Shop ID is required")
        UUID shopId
) {
}

package com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class CreatedShopDto {
    private UUID id;
    private String name;
    private String description;
    private UUID userId;
    private UUID location;
    @JsonProperty("opening_hours")
    private List<UUID> openingHours;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setLocation(UUID location) {
        this.location = location;
    }

    public void setOpeningHours(List<UUID> openingHours) {
        this.openingHours = openingHours;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getLocation() {
        return location;
    }

    public List<UUID> getOpeningHours() {
        return openingHours;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}

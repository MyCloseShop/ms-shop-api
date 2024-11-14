package com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ShopDto {
    private UUID shopId;
    private String name;
    private String description;
    private UUID ownerId;
    private List<UUID> openingHoursIds;
    private UUID locationId;
}

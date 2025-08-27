package com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating the Stripe Connect account ID of a shop
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateStripeAccountDto {
    private String stripeAccountId;
}
package com.etna.gpe.mycloseshop.ms_shop_api.services.shop;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.location.LocationDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.opening_hours.OpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreateShopWithLocationAndOpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreatedShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.ShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.UpdateStripeAccountDto;

import java.util.List;

public interface IShopService {
    CreatedShopDto createShop(CreateShopWithLocationAndOpeningHoursDto shopDto);

    List<ShopDto> getShops();

    ShopDto getShop(String shopId);

    List<OpeningHoursDto> getShopOpeningHours(String shopId);

    LocationDto getShopLocation(String shopId);

    /**
     * Récupère l'ID du compte Stripe Connect d'un shop.
     * @param shopId ID du shop
     * @return L'ID du compte Stripe connecté
     */
    String getShopStripeAccountId(String shopId);
    
    /**
     * Met à jour l'ID du compte Stripe Connect d'un shop.
     * @param shopId ID du shop
     * @param stripeAccountDto DTO contenant le nouvel ID du compte Stripe
     * @return Message de confirmation
     */
    String updateShopStripeAccountId(String shopId, UpdateStripeAccountDto stripeAccountDto);
}

package com.etna.gpe.mycloseshop.ms_shop_api.services;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.location.LocationDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.opening_hours.OpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreateShopWithLocationAndOpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreatedShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.ShopDto;

import java.util.List;

public interface IShopService {
    CreatedShopDto createShop(CreateShopWithLocationAndOpeningHoursDto shopDto);

    List<ShopDto> getShops();

    ShopDto getShop(String shopId);

    List<OpeningHoursDto> getShopOpeningHours(String shopId);

    LocationDto getShopLocation(String shopId);
}

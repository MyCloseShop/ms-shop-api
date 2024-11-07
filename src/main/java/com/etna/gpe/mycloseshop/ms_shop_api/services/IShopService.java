package com.etna.gpe.mycloseshop.ms_shop_api.services;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.CreateShopWithLocationAndOpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.CreatedShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.ShopDto;

import java.util.List;

public interface IShopService {
    CreatedShopDto createShop(CreateShopWithLocationAndOpeningHoursDto shopDto);

    List<ShopDto> getShops();

    ShopDto getShop(String shopId);
}

package com.etna.gpe.mycloseshop.ms_shop_api.controllers.shop;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.location.LocationDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.opening_hours.OpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreateShopWithLocationAndOpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreatedShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.ShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.services.IShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShopControllerImpl implements IShopController {
    private final IShopService shopService;

    public ShopControllerImpl(IShopService shopService) {
        this.shopService = shopService;
    }

    @Override
    public ResponseEntity<CreatedShopDto> createShop(CreateShopWithLocationAndOpeningHoursDto shopDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shopService.createShop(shopDto));
    }

    @Override
    public ResponseEntity<List<ShopDto>> getShops() {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.getShops());
    }

    @Override
    public ResponseEntity<ShopDto> getShop(String shopId) {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.getShop(shopId));
    }

    @Override
    public ResponseEntity<List<OpeningHoursDto>> getShopOpeningHours(String shopId) {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.getShopOpeningHours(shopId));
    }

    @Override
    public ResponseEntity<LocationDto> getShopLocation(String shopId) {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.getShopLocation(shopId));
    }
}

package com.etna.gpe.mycloseshop.ms_shop_api.controllers;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.CreateShopWithLocationAndOpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.CreatedShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.ShopDto;
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
}
